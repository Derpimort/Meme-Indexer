# Changelog
## [1.2.1](https://github.com/Derpimort/Meme-Indexer/releases/tag/v1.2.1)
13 June 2022, 12:03 am IST
### Multiselect & Usage History Control [FIXED]
#### UI/UX changes
- Clear history icon color fix
- Multiselect button placement

#### Fixes & Misc
- Playstore requirement for target API 30
- Gradle & deps version

## [1.2.0](https://github.com/Derpimort/Meme-Indexer/releases/tag/v1.2.0)
20 January 2022, 12:22 am IST
### Multiselect & Usage History Control
#### UI/UX changes
- Clear history
  - menu item
  - popup
  - preference item for persistence
- Base button style
- Select multiple memes for share
  - Long press
  - Mode toggle from menu
- Photoview for popup zoom
- Menu item background color in themes

#### Fixes & Misc
- Gradle & deps version
- Multi word search improved with MATCH & NEAR
- Repo changes
- Placeholder frags removed
- Privacy policy added

## [1.1.0](https://github.com/Derpimort/Meme-Indexer/releases/tag/v1.1.0)
16 June 2021, 01:53 am IST
### Complete rollout on playstore
- **Share meme update**
  - App now shows up as selector for documents and files
  - Returns selected image if called from other app, default functions otherwise

## [1.0.0](https://github.com/Derpimort/Meme-Indexer/releases/tag/v1.0.0)
03 June 2021, 02:06 am IST
- **UI/UX Changes**
  - Increase title text weight
  - Index fragment background and button updates
  - Waves updated
  - Multiple paths for scans
  - Index runs on non UI thread
  - First boot opens index fragment
  - Parallel queued images can be changed from settings
  - App name updated in disclaimer
  - Notif channel name update
- **Bugs Fixes**
  - Skip files instead of crashing app when not getting path
  - Workmanager request crash fix, not using applicationContext now


## [0.10.1 - Beta internal testing](https://github.com/Derpimort/Meme-Indexer/releases/tag/v0.10.1)
01 June 2021, 12:32 am IST
### Release for play store internal testing
- **Minor UI fixes**
  - search history scrollbar hide
  - Dark theme color updates
  - primary disabled text color
  - dialog instead of snackbar on index complete
  - Spacebar name and logo
- **Bugs**
  - Different workmanager UID for onetimerequests, gives error on change otherwise... ofc



## [0.10.0 - Beta](https://github.com/Derpimort/Meme-Indexer/releases/tag/v0.10.0)
23 May 2021, 2:08 am IST
- **Themes & Settings activity**
- Theme selection: 
  - Legacy
  - Dark
  - Light
- Schedule scan:
  - On/Off
  - Time selection
  - Run onetime on indexbuilder select if Off
- SharedPrefs and Preferences for settings
- FIX
  - Default color settings for themes
  - Picasso in RV bind bug



## [0.9.0 - Pre-Beta](https://github.com/Derpimort/Meme-Indexer/releases/tag/v0.9.0)
20 May 2021, 2:18 am IST
- Indexbuilder now only lists and puts filepaths in DB
- Added CoroutineWorker to scan and process files in DB using foreground service concept
- Scheduled daily scans
- Post scan summary
- Notification Icon
- Dark theme apk


## [0.8.1 - Almost ready](https://github.com/Derpimort/Meme-Indexer/releases/tag/0.8.1)
20 Apr 2021, 1:37 am IST
- About us added, basic text, social links and spacebar.
- Update gradle deps
- BUGS: 
    - API 29:
        - Helpers that used context changed to use activity now. 
        - EACCESS permission error



## [0.8 - First release](https://github.com/Derpimort/Meme-Indexer/releases/tag/v0.8)
19 Apr 2021, 1:54 am IST

- **Select folder to scan/index**
    - **Show path**
    - **No. of files scanned** live update in button
    - **Snackbar notif at end** showing scanned and total files
- **Search indexed memes**
    - **Share button.** *Using FileProvider*
    - **Preview on click** with return and share option
    - **Info** button shows dialog
        - **Circle preview and filename** in dialog title        
        - **Show Path**, clickable to view file in explorer/gallery
        - **Edit the ocr text**
    - **Recent search** history bar with clickable *re-searches*
    - Hide search bar and bottom toolbar on scroll
- **History tab** *showing recent shared memes*, Preview similar to search on click
- **Dark theme** according to overhaul
- **Send feedback button**
    -  Simple text dialog
    - Mail apps intent on send with subject, body and "To" mail added
