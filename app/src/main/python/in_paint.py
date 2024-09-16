
from pyinpaint import Inpaint
from PIL import Image
import cv2
import numpy as np
import os


# TODO This method is used for return byte data without convert into mat mask using open cv

def inpaint_image(org_img_path, mask_path):
    # Read the mask image using OpenCV
    mask_img = cv2.imread(mask_path, cv2.IMREAD_GRAYSCALE)

    # Convert the grayscale mask to binary using a threshold
    _, binary_mask = cv2.threshold(mask_img, 127, 255, cv2.THRESH_BINARY)

    # Save the binary mask image to a temporary file
    binary_mask_path = "temp_binary_mask.png"
    cv2.imwrite(binary_mask_path, binary_mask)

    # Initialize Inpaint with the original image and the binary mask file path
    inpaint = Inpaint(org_img_path, binary_mask_path)
    inpainted_img = inpaint()

    # Convert the inpainted image to uint8 format
    inpainted_img = (inpainted_img * 255).astype(np.uint8)
    image = Image.fromarray(inpainted_img)

    # Save the inpainted image
    image.save("inpainted.png")

    # Optionally remove the temporary binary mask file
    os.remove(binary_mask_path)



# TODO This method is used for return byte data without convert into mat mask using open cv

# def in_paint_image(org_img_path, mask_path):
#     inpaint = Inpaint(org_img_path, mask_path)
#     inpainted_img = inpaint()
#
#     # Ensure both images are in the same format
#     inpainted_img = remove_alpha_channel(inpainted_img)
#
#     # Convert the inpainted image to uint8 format
#     inpainted_img = (inpainted_img * 255).astype(np.uint8)
#     image = Image.fromarray(inpainted_img)
#
#     # Convert the image to a byte array
#     byte_io = io.BytesIO()
#     image.save(byte_io, format="PNG")
#     byte_data = byte_io.getvalue()
#
#     # Return the byte data instead of saving it to a file
#     return byte_data


def remove_alpha_channel(image):
    if image.shape[0] == 4:  # If it has 4 channels (RGBA)
        return image[:3, :, :]  # Keep only the first 3 channels (RGB)
    return image  # Return as is if it's already RGB

# TODO This method is used for save image in cache directory without convert into mat mask using open cv

def in_paint_image(org_img_path, mask_path):
    inpaint = Inpaint(org_img_path, mask_path)
    inpainted_img = inpaint()
    # Convert the inpainted image to uint8 format
    inpainted_img = (inpainted_img * 255).astype(np.uint8)
    image = Image.fromarray(inpainted_img)
    # Specify the directory where the image will be saved
    save_dir = "/data/user/0/com.chaquo.myapplication/cache/"

    # Ensure the directory exists
    if not os.path.exists(save_dir):
        os.makedirs(save_dir)
    # Save the image with a full path
    image.save(os.path.join(save_dir, "inpainted.png"))
