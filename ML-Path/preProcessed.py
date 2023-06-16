import cv2
import numpy as np
import re
import pytesseract
from skimage.filters import threshold_local
from PIL import Image
from pytesseract import Output
from prettytable import PrettyTable

# PreProcess the image
# Resize Image


def resizeImage(image, ratio):
    width = int(image.shape[1] * ratio)
    height = int(image.shape[0] * ratio)
    dim = (width, height)
    return cv2.resize(image, dim, interpolation=cv2.INTER_AREA)


def approxContour(contour):
    peri = cv2.arcLength(contour, True)
    return cv2.approxPolyDP(contour, 0.032 * peri, True)

# Find 4 corners in the image


def getContour(contours):
    # Loop through all contours
    for c in contours:
        approx = approxContour(c)
        # If the contour has 4 corners, it can be concluded that it is the desired contour (receipt contour)
        if len(approx) == 4:
            return approx

# Convert 4 corners into connected lines forming a rectangle


def contourToRect(contour):
    resizeRatio = 500.0 / contour.shape[0]
    points = contour.reshape(4, 2)
    rectangle = np.zeros((4, 2), dtype=np.float32)
    s = points.sum(axis=1)
    rectangle[0] = points[np.argmin(s)]
    rectangle[2] = points[np.argmax(s)]
    # Calculate the difference between corner points
    # The top-right corner has a smaller difference
    # The bottom-left corner has a larger difference
    difference = np.diff(points, axis=1)
    rectangle[1] = points[np.argmin(difference)]
    rectangle[3] = points[np.argmax(difference)]
    return rectangle / resizeRatio

# WarpPerspective


def warpPerspective(image, rectangle):
    # Get the corner points
    (tl, tr, br, bl) = rectangle
    # Calculate the width of the new image
    widthA = np.sqrt(((br[0] - bl[0]) ** 2) + ((br[1] - bl[1]) ** 2))
    widthB = np.sqrt(((tr[0] - tl[0]) ** 2) + ((tr[1] - tl[1]) ** 2))
    # Calculate the height of the new image
    heightA = np.sqrt(((tr[0] - br[0]) ** 2) + ((tr[1] - br[1]) ** 2))
    heightB = np.sqrt(((tl[0] - bl[0]) ** 2) + ((tl[1] - bl[1]) ** 2))
    # Get the maximum values of width and height
    maxWidth = max(int(widthA), int(widthB))
    maxHeight = max(int(heightA), int(heightB))
    # Define the corner points of the new image
    dst = np.array([
        [0, 0],
        [maxWidth - 1, 0],
        [maxWidth - 1, maxHeight - 1],
        [0, maxHeight - 1]], dtype=np.float32)
    # Calculate the transformation matrix
    matrix = cv2.getPerspectiveTransform(rectangle, dst)
    # WarpPerspective
    return cv2.warpPerspective(image, matrix, (maxWidth, maxHeight))

# Threshold Image


def blackAndWhite(image):
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    # Display grayscale image
    T = threshold_local(gray, 21, offset=5, method="gaussian")
    return (gray > T).astype("uint8") * 255

# Find amounts


def findAmounts(text):
    amounts = re.findall(r'\d+\.\d{2}\b', text)
    floats = [float(amount) for amount in amounts]
    unique = list(dict.fromkeys(floats))
    return unique
