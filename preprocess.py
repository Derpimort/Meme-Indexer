#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sat Mar 21 20:13:50 2020

@author: darp_lord
"""
import argparse
import os
import cv2
import sys
from PIL import Image, ImageSequence



class ProgressBar():
	def __init__(self,n_files):
		self.total_files=n_files
		self.curr_counter=0
		self.multiplier=0
	def increment(self):
		self.curr_counter+=1
		self.multiplier=(self.curr_counter*30)//self.total_files
		sys.stdout.write('\r')
		sys.stdout.write("[%-30s] %d/%d" % ('='*self.multiplier, self.curr_counter,self.total_files))
		sys.stdout.flush()

def checkDir(dirname):
	if not os.path.exists(dirname):
		os.makedirs(dirname)

def getNumberOfFiles(directory):
	extensions={}
	for _,_,filenames in os.walk(directory):
		for filename in filenames:
			ext=os.path.splitext(filename)[1]
			if ext not in extensions:
				extensions[ext]=1
			else:
				extensions[ext]+=1
	return extensions

def printData(src_dir, dst_dir,ext_dict, output_suffix):
	print(f"Source: {src_dir}")
	print(f"Destination: {dst_dir}")
	print(f"Output files suffix: {output_suffix}")
	
	print("\nNumber of files in source directory:")
	for extension in ext_dict:
		print(f"{extension} : {total_files[extension]}")
	
	print()

def save_gif_selective(fname,save_dir, target_size, output_suffix=".jpg"):
	im = Image.open(fname)
	fname=fname.split("/")[-1][:-4]
	save_dir=os.path.join(save_dir,fname)
	im_len=sum(1 for _ in ImageSequence.Iterator(im))
	req_frames=[0,im_len//2,im_len-1]
	index=0
	for frame in ImageSequence.Iterator(im):
		if index in req_frames:
			fname=save_dir+"_%04d.png" % index
			frame.save(fname)
			dfname="".join(fname.split(".")[:-1])+output_suffix
			im=cv2.imread(fname)
			im=cv2.resize(im, target_size, interpolation=cv2.INTER_CUBIC)
			cv2.imwrite(dfname ,im)
			os.remove(fname)
		index += 1

def save_gif_all(fname,save_dir):
	im = Image.open(fname)
	fname=fname.split("/")[-1][:-4]
	save_dir=os.path.join(save_dir,fname)
	checkDir(save_dir)
	index = 1
	for frame in ImageSequence.Iterator(im):
		frame.save(save_dir+"/%04d.png" % index)
		index += 1

def preProcess(src_dir, dst_dir, target_size=(224,224), ext_dict=None, output_suffix=".jpg"):
	
	if ext_dict is None:
		ext_dict=getNumberOfFiles(src_dir)
		printData(src_dir, dst_dir, ext_dict, output_suffix)
	total_files=sum(ext_dict.values())
	progress=ProgressBar(total_files)
	failed=[]
	for dirname, _, filenames in os.walk(src_dir):
		for filename in filenames:
			try:
				if (filename.endswith(".gif")):
					save_gif_selective(os.path.join(dirname, filename), dst_dir, target_size, output_suffix)
				else:
					dfname="".join(filename.split(".")[:-1])+output_suffix
					im=cv2.imread(os.path.join(dirname, filename))
					im=cv2.resize(im, target_size, interpolation=cv2.INTER_CUBIC)
					cv2.imwrite(os.path.join(dst_dir,dfname) ,im)
				progress.increment()
			except Exception as e:
				failed.append((os.path.join(dirname,filename),e))

	print("\nSuccessfully resize %d files"%progress.curr_counter)
	print("%d Failed "%len(failed))
	for i,e in failed:
		print(i,e)


if __name__ == "__main__":
	parser=argparse.ArgumentParser(
		description="Resize all images in source directory and copy them to destination directory")
	
	parser.add_argument(
		"--src",
		"-s", 
		help="Source directory with all images",
		required=True)
	
	parser.add_argument(
		"--dst", 
		"-d",
		help="Destination directory, default is current working directory")
	
	parser.add_argument(
		"--output-suffix",
		"-e",
		help="Specify extension for output files, default=.jpg",
		default=".jpg",
		choices=[".png",".jpg"])
	args=parser.parse_args()
	target_size=(224,224)
	try:
		target_size=eval(input("Target size (width,height): "))
	except:
		print("Invalid size input, defaulting to (224,224)")
		
	source_dir=os.path.abspath(args.src)
	destination_dir=os.getcwd()
	if args.dst is not None:
		destination_dir=os.path.abspath(args.dst)
	destination_dir=os.path.join(destination_dir,"Preprocessed_imgs")
	output_suffix=args.output_suffix
	total_files=getNumberOfFiles(source_dir)
	printData(source_dir, destination_dir, total_files, output_suffix)
	
	yes=["y","yes",""]
	
	proceed=input("\n\nProceed [y]/n:").lower()
	
	if proceed in yes:
		checkDir(destination_dir)
		preProcess(source_dir, destination_dir, target_size,total_files, output_suffix)
	else:
		print("Aborted")