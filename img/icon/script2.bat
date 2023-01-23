for %%f in (result\*_final.png) do (
    magick convert "%%f" mask\mask3.png -gravity center -composite -resize 18x18 result\%%~nf_finalfinal.png
    
)