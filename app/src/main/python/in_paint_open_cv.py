import cv2
import numpy as np


def in_paint_open_cv(image_path, mask_path, inpaint_output_path, threshold=127):
    # Load the original image
    img = cv2.imread(image_path)

    # Load the mask image as grayscale
    mask = cv2.imread(mask_path, cv2.IMREAD_GRAYSCALE)

    # Convert the mask to binary
    binary_mask = convert_to_binary(mask, threshold)

    # Apply inpainting using the binary mask
    inpainted_img = cv2.inpaint(img, binary_mask, inpaintRadius=3, flags=cv2.INPAINT_NS)

    # Save the inpainted image
    cv2.imwrite(inpaint_output_path, inpainted_img)
    print(f"Inpainted image saved as '{inpaint_output_path}'")



def convert_to_binary(mask, threshold=127):

    # Apply a binary threshold to create a binary mask
    _, binary_mask = cv2.threshold(mask, threshold, 255, cv2.THRESH_BINARY)

    # Invert the binary mask: black (0) becomes white (255) and vice versa
    binary_mask = cv2.bitwise_not(binary_mask)

    return binary_mask


# Example usage:
# apply_inpainting('original_image.png', 'mask_image.png', 'inpainted_image.jpg', threshold=127)
