#!/bin/sh

# script to generate valid and some invalid tar file samples

# create a valid tar file
tar cf valid.tar ../pom.xml

# create a tar file with an invalid mtime field
# (the checksum is not recomputed and therefore is invalid too)
cp valid.tar invalid_mtime.tar
printf 8 > broken_mtime.field
dd if=broken_mtime.field of=invalid_mtime.tar bs=1 seek=136 conv=notrunc
rm broken_mtime.field

# create a tar file with an invalid checksum field
cp valid.tar invalid_checksum.tar
printf 424242 > broken_checksum.field
dd if=broken_checksum.field of=invalid_checksum.tar bs=1 seek=148 conv=notrunc
rm broken_checksum.field

# create a tar file with an invalid file mode
# (the checksum is not recomputed and therefore is invalid too)
cp valid.tar invalid_filemode.tar
printf 0107777 > broken_filemode.field
dd if=broken_filemode.field of=invalid_filemode.tar bs=1 seek=100 conv=notrunc
rm broken_filemode.field
