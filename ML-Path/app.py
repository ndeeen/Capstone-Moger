from flask import Flask, request, jsonify
import cv2
from preProcessed import *
import json

app = Flask(__name__)


@app.route('/processImage', methods=['POST'])
def processImage():
    # Check if the request is containing an image
    if 'file' not in request.files:
        return jsonify({'error': 'No file found'})

    # Get the image
    file = request.files['file']

    # read the image file using OpenCV
    image = cv2.imdecode(np.fromstring(
        file.read(), np.uint8), cv2.IMREAD_UNCHANGED)

    # Do the processing here
    resizeRatio = 500 / image.shape[0]
    originalimage = image.copy()
    image = resizeImage(image, resizeRatio)

    # Convert to grayscale
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # Blur the image
    blur = cv2.GaussianBlur(gray, (5, 5), 0)

    # Define the kernel for dilation
    rectangleKernel = cv2.getStructuringElement(cv2.MORPH_RECT, (9, 9))

    # Applying dilation to the blurred image
    dilated = cv2.dilate(blur, rectangleKernel)

    # Apply cannyn edge detection
    edge = cv2.Canny(dilated, 20, 125, apertureSize=3)

    # Detect all of the contours
    contours, hierarchy = cv2.findContours(
        edge.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    # Get contours with largest area
    largestContour = sorted(contours, key=cv2.contourArea, reverse=True)[:1]

    getContour(largestContour)

    if (getContour == None):
        return jsonify({'error': 'Please take a clearer picture'})

    receiptContour = getContour(largestContour)

    # Warp the perspective of the image based on the receipt contour
    scannedContour = warpPerspective(
        originalimage.copy(), contourToRect(receiptContour))

    # Threshold the image
    processedImage = blackAndWhite(scannedContour)

    return processedImage


def performOCR(image):
    # Perform OCR and get bounding box information
    part = pytesseract.image_to_data(image, output_type=Output.DICT)

    # Get the number of detected boxes
    nBoxes = len(part['level'])

    # Create an RGB copy of the image for visualization
    boxes = cv2.cvtColor(image.copy(), cv2.COLOR_BGR2RGB)

    # Iterate over each detected box and draw a rectangle around it
    for i in range(nBoxes):
        # Get the coordinates and dimensions of the box
        (x, y, w, h) = (part['left'][i], part['top']
                        [i], part['width'][i], part['height'][i])

    # Draw a rectangle around the box
    boxes = cv2.rectangle(boxes, (x, y), (x + w, y + h), (0, 255, 0), 2)

    # Set custom configuration for Tesseract OCR
    customConfig = r'--oem 3 --psm 6'

    # Perform OCR on the image using the custom configuration
    text = pytesseract.image_to_string(image, config=customConfig)

    # Apply regex
    # List of exceptions - words to be excluded from the extracted text
    exceptList = ["Subtotal", "Subtotal", "Total", "Tax", "Total Due", "Total Amount",
                  "Amount Due", "Amount", "Due", "Change", "change", "Kembali", "kembali", "Tunai", "Qty"]

    # List of words to be removed from the extracted text
    removedList = ['vit', 'etc']

    # Extract letters and numbers regex
    regex_line = []
    for line in text.splitlines():
        if re.search(r"[0-9]*\.[0-9]|[0-9]*\,[0-9]", line):
            regex_line.append(line)

    # Apply exclusion list
    item = []
    for eachLine in regex_line:
        found = False
        for exclude in exceptList:
            if exclude in eachLine.lower():
                found = True

        if found == False:
            item.append(eachLine)

    # Apply removed list
    newItemList = []
    for i in item:
        for subToRemove in removedList:
            i = i.replace(subToRemove, "")
            i = i.replace(subToRemove.upper(), "")
        newItemList.append(i)

    # Item cost regex
    item_cost = []
    for line in newItemList:
        line = line.replace(",", ".")
        cost = re.findall('\d*\.?\d+|\d*\,?\d+|', line)

        for possibleCost in cost:
            if "." in possibleCost:
                item_cost.append(possibleCost)

    # Counter for the number of items
    count = 0

    # List to store only the items without cost
    onlyItems = []

    # Iterate over each item in the 'newItemList'
    for item in newItemList:
        # Variable to store only alphabets and spaces
        only_alpha = ""

        # Iterate over each character in the item
        for char in item:
            # Check if the character is alphabetic or a space
            if char.isalpha() or char.isspace():
                only_alpha += char

        # Remove single-letter words using regular expression substitution
        only_alpha = re.sub(r'(?:^| )\w(?:$| )', ' ', only_alpha).strip()

        # Append the modified item to the 'onlyItems' list
        onlyItems.append(only_alpha)

    # List to store the modified items
    thing = []

    # Iterate over each item in 'onlyItems'
    for item in onlyItems:
        # Split the item into individual words
        temp = item.split()

        # Omit words with length 2
        res = [ele for ele in temp if len(ele) != 2]

        # Join the remaining words back into a string
        res = ' '.join(res)

        # Append the modified item to the 'thing' list
        thing.append(res)

    itemWithCosts = []

    # iterate over each item in thing and item_cost
    for thing, item_cost in zip(thing, item_cost):
        # Append the item and its cost to the 'itemWithCost' list
        itemWithCost = {
            "item": thing,
            "cost": item_cost
        }
        itemWithCosts.append(itemWithCost)

    # convert the 'itemWithCosts' list to a JSON string
    itemWithCosts = json.dumps(itemWithCosts)

    # return the JSON string
    return jsonify(itemWithCosts)
