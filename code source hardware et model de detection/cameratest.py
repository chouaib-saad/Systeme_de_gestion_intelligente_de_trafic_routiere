import cv2
import numpy as np
import time
import pyrebase
import threading

config = {
    "apiKey": "YOUR_API_KEY",
    "authDomain": "YOUR_AUTH_DOMAIN",
    "databaseURL": "YOUR_DATABASE_URL",
    "storageBucket": "YOUR_STORAGE_BUCKET"
}

firebase = pyrebase.initialize_app(config)
db = firebase.database()

video_capture_sources = [
    "http://192.168.0.21/mjpeg/1",  # Update with your camera stream URLs
    "http://192.168.0.22/mjpeg/1",
    "http://192.168.0.23/mjpeg/1",
    "http://192.168.0.24/mjpeg/1"
]

camera_names = ["Camera 1", "Camera 2", "Camera 3", "Camera 4"]

# Load YOLO
net = cv2.dnn.readNet("yolov3.weights", "yolov3.cfg")
classes = []
with open("coco.names", "r") as f:
    classes = [line.strip() for line in f.readlines()]
layer_names = net.getLayerNames()
output_layers = [layer_names[i - 1] for i in net.getUnconnectedOutLayers()]
colors = np.random.uniform(0, 255, size=(len(classes), 3))

car_counts = [0] * len(camera_names)

def update_iot(index, count):
    db.child("traffic").child(camera_names[index]).update({"vehicle-count": str(count)})

def process_camera(index, video_capture_source):
    cap = cv2.VideoCapture(video_capture_source)
    while cap.isOpened():
        ret, frame = cap.read()
        if not ret:
            break

        height, width, channels = frame.shape

        blob = cv2.dnn.blobFromImage(frame, 0.00392, (416, 416), (0, 0, 0), True, crop=False)

        net.setInput(blob)
        outs = net.forward(output_layers)

        class_ids = []
        confidences = []
        boxes = []

        for out in outs:
            for detection in out:
                scores = detection[5:]
                class_id = np.argmax(scores)
                confidence = scores[class_id]
                if confidence > 0.5:
                    center_x = int(detection[0] * width)
                    center_y = int(detection[1] * height)
                    w = int(detection[2] * width)
                    h = int(detection[3] * height)
                    x = int(center_x - w / 2)
                    y = int(center_y - h / 2)
                    boxes.append([x, y, w, h])
                    confidences.append(float(confidence))
                    class_ids.append(class_id)

        indexes = cv2.dnn.NMSBoxes(boxes, confidences, 0.5, 0.4)

        font = cv2.FONT_HERSHEY_PLAIN
        for i in range(len(boxes)):
            if i in indexes:
                label = str(classes[class_ids[i]])
                if label in ["bicycle", "car", "motorcycle", "bus", "truck"]:
                    x, y, w, h = boxes[i]
                    color = colors[class_ids[i]]
                    cv2.rectangle(frame, (x, y), (x + w, y + h), color, 2)
                    cv2.putText(frame, label, (x, y + 30), font, 3, color, 3)
                    car_counts[index] += 1

        update_iot(index, car_counts[index])

        cv2.imshow(camera_names[index], frame)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()

def start_camera_threads():
    threads = []
    for i, source in enumerate(video_capture_sources):
        t = threading.Thread(target=process_camera, args=(i, source))
        threads.append(t)
        t.start()

    for t in threads:
        t.join()

if __name__ == "__main__":
    start_camera_threads()
