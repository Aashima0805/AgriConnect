import os
import sys
from flask import Flask, request, jsonify
from flask_cors import CORS
from PIL import Image
from ultralytics import YOLO

app = Flask(__name__)
CORS(app)

# Configuration
MODEL_PATH = os.environ.get("AI_MODEL_PATH", os.path.join(os.path.dirname(__file__), "models", "best.pt"))
CONFIDENCE_THRESHOLD = float(os.environ.get("AI_CONFIDENCE_THRESHOLD", 0.50))
ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png"}

# Verify and Load YOLO11n Model at Startup
if not os.path.exists(MODEL_PATH):
    print(f"FATAL ERROR: Trained YOLO model file not found at: {MODEL_PATH}", file=sys.stderr)
    sys.exit(1)

try:
    print(f"Loading YOLO11n model from: {MODEL_PATH}...")
    model = YOLO(MODEL_PATH)
    print(f"Model loaded successfully! Detected classes: {model.names}")
except Exception as e:
    print(f"FATAL ERROR: Failed to load YOLO model: {e}", file=sys.stderr)
    sys.exit(1)


def is_allowed_file(filename):
    return "." in filename and filename.rsplit(".", 1)[1].lower() in ALLOWED_EXTENSIONS


@app.route("/health", methods=["GET"])
def health():
    return jsonify({
        "status": "ok",
        "model": "YOLO11n Rice Leaf Disease",
        "model_path": MODEL_PATH,
        "classes": list(model.names.values()) if hasattr(model, "names") else [],
        "confidence_threshold": CONFIDENCE_THRESHOLD
    })


@app.route("/predict", methods=["POST"])
def predict():
    if "image" not in request.files:
        return jsonify({
            "status": "error",
            "message": "Missing required 'image' file in multipart/form-data request."
        }), 400

    file = request.files["image"]
    if not file or file.filename == "":
        return jsonify({
            "status": "error",
            "message": "Empty file provided. Please upload a valid rice leaf image."
        }), 400

    if not is_allowed_file(file.filename):
        return jsonify({
            "status": "error",
            "message": "Invalid file format. Only JPG, JPEG, and PNG images are supported."
        }), 400

    crop = request.form.get("crop", "Rice")

    try:
        image = Image.open(file.stream).convert("RGB")
    except Exception as e:
        return jsonify({
            "status": "error",
            "message": f"Failed to decode image file: {str(e)}"
        }), 400

    # Run genuine YOLO inference
    try:
        results = model.predict(source=image, conf=0.25, verbose=False)
    except Exception as e:
        return jsonify({
            "status": "error",
            "message": f"Inference error: {str(e)}"
        }), 500

    detections = []
    top_disease = None
    top_confidence = 0.0

    if results and len(results) > 0:
        res = results[0]
        boxes = res.boxes
        if boxes is not None and len(boxes) > 0:
            for box in boxes:
                cls_id = int(box.cls[0].item())
                conf = float(box.conf[0].item())
                label = model.names.get(cls_id, f"Class_{cls_id}")
                xyxy = [float(v) for v in box.xyxy[0].tolist()]

                detections.append({
                    "class_id": cls_id,
                    "disease": label,
                    "confidence": round(conf, 4),
                    "bbox": xyxy
                })

                if conf > top_confidence:
                    top_confidence = conf
                    top_disease = label

    # Check against configurable threshold
    if top_confidence < CONFIDENCE_THRESHOLD or top_disease is None:
        return jsonify({
            "status": "low_confidence",
            "crop": crop,
            "confidence": round(top_confidence, 4),
            "message": "The uploaded image could not be classified reliably. Please upload a clear, well-lit photo of the affected rice leaf.",
            "detections": detections
        }), 200

    return jsonify({
        "status": "success",
        "crop": crop,
        "disease": top_disease,
        "confidence": round(top_confidence, 4),
        "detections": detections
    }), 200


if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))
    print(f"Starting AgriConnect AI Microservice on http://0.0.0.0:{port}...")
    app.run(host="0.0.0.0", port=port, debug=False)
