UStarInfo 0.0.1
===============

The aim of this tool is to print the information from the tar headers. This is especially helpful in cases where you need to restore a tar archive but tar refuses to process it.

`file` probably identifies the file as a tar archive ...

```
$ file somefile.tar someother.tar
somefile.tar : POSIX tar archive
someother.tar: POSIX tar archive (GNU)
```
... but later on tar refuse to process it.

```
$ tar -tvf somefile.tar
tar: This does not look like a tar archive
tar: Skipping to next header
tar: Exiting with failure status due to previous errors
```

The error message doesn't explain in detail what's wrong. The most probable reason is, that the checksum of the header block doesn't match with the header data. 

This tool display the header information of all entries and helps to indentify such cases. An error is reported if:

 - the computed checksum doesn't match the one reported in the header
 - the last modification time is invalid

Usage
-----

`java -jar UStarInfo.jar somefile.tar`

**example output**

```
     header offset: 0  dec.  0 hex
            format: POSIX tar (GNU)
     UStar version:  
         file name: pom.xml
         file mode: 0000644
          owner ID: 0001750
          group ID: 0001750
      size (octal): 00000003144
             mtime: ERROR: mtime is not a valid octal number 82633110307
 checksum (header): ERROR: checksum in the header 013511 is not equal to the computed
 checksum computed: unsigned: 013520  signed: 013520
          typeFlag: 0
         link name: 
        owner name: suboptimal
        group name: suboptimal
      device major: 
      device minor: 
  file name prefix: 
```

Building UStarInfo from Source
------------------------------

```
git clone http://github.com/SubOptimal/UStarInfo.git
cd UStarInfo/
mvn clean package
```

After the build the executable jar is placed in the `target/` directory.

Running on example tar files
----------------------------

```
cd samples/
./create_samples.sh  # create valid and invalid tar file samples
./run_samples.sh     # execute UStarInfo on the created tar file samples
```
