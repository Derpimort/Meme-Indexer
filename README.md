# Meme-Indexer
Generate a txt/Index of all Memes in the given directory and its subdirs
## Ongoing 1: OCR
  - [ ] Find production level balanced technique for OCR.
  - [ ] Sub image separation and retrieval.
  - [ ] Caption/describe each image (like insta).
  
## Ongoing 2: Analysis
  - [x] Feature extraction with inceptionV3
    > Pins first
  - [x] ~~Try clustering~~ is useless. Better use image similarity with the knowyourmeme database.
    > Pins only
  - [ ] NLP with feature extraction
    > 9gag memes
  - [ ] OCR on pinterest memes
    > If nlp is successful on 9gag
  - [ ] Finalize pipeline
    > OCR-->299x299 PP-->Feature extraction-->Clustering? NLP?

### Current Plan:
  - [x] **Dataset:**
    - Separate memes from regular pics in all my devices to get a random dataset
    - Or...... make a knowyourmeme scrapper
  
  - [ ] **Analytics:**
    - Will clustering work? Try to get results and group together similar memes
    - Separate clear text field memes and shit ones
    - OOOOORRRRR.... Textboxes and preprocess the shit outta them?? May save some time? idk
  - [ ] **Index:**
    - Based on text
    - Based on template
  - [ ] **Deploy:**
    - Python module
    - Android app
  
## DONE!: Dataset
  - [x] Collected my 2012 memes from 9gag(800) and pinterest(4000)
    > Pinterest memes have consistent format: all jpg... but random names. 9gag memes vary in format but filename relate to title.
  - [x] Preprocess all Pinterest files to 299x299
  - [x] Process 9gag files
    > gifs, frame by frame. Rest all cv will take care of
  - [x] All frames of gif or selected intervals?
    > first, mid, last frames saved as jpg
