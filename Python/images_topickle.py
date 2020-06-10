#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Mar 25 20:55:26 2020

@author: darp_lord
"""

import os
import pandas as pd
import numpy as np
import cv2

def checkDir(dirname):
	if not os.path.exists(dirname):
		os.makedirs(dirname)
		
def convertToDataframe(data_dir):
	images=[]
	img_path=[]
	for dirname, _, filenames in os.walk(data_dir):
		print(dirname, len(filenames))
		for filename in filenames:
			try:
				f_path=os.path.join(dirname, filename)
				images.append(cv2.imread(f_path)[:,:,::-1])
				img_path.append(f_path)
			except Exception as e:
				print(filename, e)

	pics=pd.DataFrame({
		"Path":img_path,
		"Images": images
	})
	return pics

if __name__=="__main__":
	convertToDataframe("Data/PP_pins_vgg").to_pickle("Data/pins_RGB_vgg.pkl")