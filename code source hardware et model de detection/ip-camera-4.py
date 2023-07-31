import cv2
import numpy as np
import time
import pyrebase
import threading

video_capture_source = "http://192.168.43.13/mjpeg/1"
nameofthelane = "lane4"

config = {
    "apiKey": "AIzaSyDfOJTuyd6MfrDOtamNwNBq34wsvm4IQaI",
    "authDomain": "traffic-control-system-84dd6.firebaseapp.com",
    "databaseURL": "https://traffic-control-system-84dd6-default-rtdb.firebaseio.com",
    "storageBucket": "traffic-control-system-84dd6.appspot.com"
}

firebase = pyrebase.initialize_app(config)

db = firebase.database()


def update_iot(count):
    db.child("traffic").child(nameofthelane).update({"vehicle-count": str(count)})


# print("I started here")
# cap = cv2.VideoCapture(video_capture_source)
# cap = cv2.VideoCapture("http://192.168.43.1:8080/video")
# cap.open("http://192.168.43.1:8080/video")
# Load Yolo
net = cv2.dnn.readNet("yolov3.weights", "yolov3.cfg")
classes = []
with open("coco.names", "r") as f:
    classes = [line.strip() for line in f.readlines()]
layer_names = net.getLayerNames()
output_layers = [layer_names[i[0] - 1] for i in net.getUnconnectedOutLayers()]
colors = np.random.uniform(0, 255, size=(len(classes), 3))
car_count = 0

interested_objects = ["bicycle", "car", "motorcycle", "bus", "truck"]

print("I AM HERE")

img = ""

cap = cv2.VideoCapture(video_capture_source)


def camera_thread():
    global cap
    while cap.isOpened():
        while True:
            print("From thread : data is captured")
            ret, frames = cap.read()
            global img
            img = cv2.rotate(frames, cv2.ROTATE_180)
            img = cv2.flip(img, 1)

previous_count = 0
if __name__ == "__main__":
    cameraThread = threading.Thread(target=camera_thread)
    cameraThread.start()
    while True:
        while cap.isOpened():
            if img != "":
                # img = cv2.resize(img, None, fx=0.4, fy=0.4)
                height, width, channels = img.shape

                # Detecting objects
                blob = cv2.dnn.blobFromImage(img, 0.00392, (416, 416), (0, 0, 0), True, crop=False)

                net.setInput(blob)
                outs = net.forward(output_layers)

                # Showing informations on the screen
                class_ids = []
                confidences = []
                boxes = []
                for out in outs:
                    for detection in out:
                        scores = detection[5:]
                        class_id = np.argmax(scores)
                        confidence = scores[class_id]
                        if confidence > 0.5:
                            # Object detected
                            center_x = int(detection[0] * width)
                            center_y = int(detection[1] * height)
                            w = int(detection[2] * width)
                            h = int(detection[3] * height)

                            # Rectangle coordinates
                            x = int(center_x - w / 2)
                            y = int(center_y - h / 2)

                            boxes.append([x, y, w, h])
                            confidences.append(float(confidence))
                            class_ids.append(class_id)

                indexes = cv2.dnn.NMSBoxes(boxes, confidences, 0.5, 0.4)
                print(indexes)
                font = cv2.FONT_HERSHEY_PLAIN
                for i in range(len(boxes)):
                    if i in indexes:
                        label = str(classes[class_ids[i]])
                        if label in interested_objects:
                            x, y, w, h = boxes[i]
                            color = colors[class_ids[i]]
                            cv2.rectangle(img, (x, y), (x + w, y + h), color, 2)
                            cv2.putText(img, label, (x, y + 30), font, 3, color, 3)
                            car_count = car_count + 1

                if previous_count == 0 and car_count == 0:
                    iot = threading.Thread(target=update_iot, args=(car_count,))
                    iot.start()

                print("The car count is " + str(car_count))
                previous_count = car_count
                if car_count != 0:
                    iot = threading.Thread(target=update_iot, args=(car_count,))
                    iot.start()
                car_count = 0
                print("Camera capture has begun and data is obtained")
                cv2.imshow("Traffic at "+nameofthelane, img)
                cv2.waitKey(1)

    cv2.destroyAllWindows()
