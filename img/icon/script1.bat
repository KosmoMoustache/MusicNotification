@echo off
for %%f in (cover\*.jpg) do (
    set input_file=%%~nf
    magick convert "%%f" mask\mask1.png -alpha off -compose copy_opacity -composite result\%%~nf_step1.png
    magick convert result\%%~nf_step1.png png8:result\%%~nf_step2.png
    magick composite -dissolve 80%% mask\mask2.png result\%%~nf_step2.png result\%%~nf_final.png
)

