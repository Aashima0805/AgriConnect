# AgriConnect AI Crop Disease Detection Microservice

This microservice provides genuine AI/ML inference for rice leaf disease detection using a custom-trained **YOLO11n** model (`best.pt`).

## Model Details
- **Architecture**: YOLO11n (Ultralytics)
- **Task**: Object Detection
- **Dataset**: Rice Leaf Disease Dataset
- **Trained Classes (6)**:
  - `0`: Bacterial Leaf Blight
  - `1`: Brown Spot
  - `2`: Healthy
  - `3`: Leaf Blast
  - `4`: Leaf scald
  - `5`: Narrow Brown Spot
- **Model Path**: `models/best.pt`

## Running the Service
```bash
python -m pip install -r requirements.txt
python app.py
```
Default server URL: `http://localhost:5000`

## API Endpoints
- `GET /health`: Health status and loaded model details.
- `POST /predict`: Upload image via multipart/form-data with field `image` (and optional `crop`).
