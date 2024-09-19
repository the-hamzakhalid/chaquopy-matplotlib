from PIL import Image
from simple_lama_inpainting import SimpleLama
import numpy as np
import cv2


def convert_to_binary(mask, threshold=127):
    """
    Convert the mask image to a binary mask and save it.

    Args:
        mask (PIL.Image): The mask image to be converted.
        threshold (int): The threshold value for binary conversion.

    Returns:
        str: Path to the saved binary mask image.
    """
    # Convert PIL image to a numpy array
    mask_np = np.array(mask)

    # Apply a binary threshold to create a binary mask
    _, binary_mask = cv2.threshold(mask_np, threshold, 255, cv2.THRESH_BINARY)
    binary_mask = cv2.bitwise_not(binary_mask)
    # Convert the binary mask back to a PIL image
    binary_mask_pil = Image.fromarray(binary_mask.astype(np.uint8))
    binary_mask_path = "binary_mask.png"
    binary_mask_pil.save(binary_mask_path)

    return binary_mask_path

def in_paint_lama(img_path, mask_path, result_path="inpainted.png"):
    """
    Perform inpainting on an image using a mask and save the result.

    Args:
        img_path (str): Path to the input image.
        mask_path (str): Path to the mask image.
        result_path (str): Path where the inpainted image will be saved.
    """
    # Initialize SimpleLama
    simple_lama = SimpleLama()

    # Open the image and mask
    image = Image.open(img_path).convert('RGB')  # Ensure the image is RGB
    mask = Image.open(mask_path).convert('L')   # Convert the mask to grayscale

    # Convert mask to binary, save it, and get the path
    binary_mask_path = convert_to_binary(mask)

    # Open the saved binary mask image
    binary_mask = Image.open(binary_mask_path).convert('L')

    # Perform inpainting
    result = simple_lama(image, binary_mask)

    # Save the result
    result.save(result_path)



# Example usage

#img_path = "original_image.png"
#mask_path = "mask_output.png"
#inpaint_image(img_path, mask_path)