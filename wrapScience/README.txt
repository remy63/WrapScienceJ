This directory is the root of the default directory structure
for data, files, resources  and metadata for the WrapScience
Software Platform.

Unless WrapScience has been specifically configured, this
directory, with the name "wrapScience", is to be place in
the "Images" directory at the root of the user's home
directory on a desktop PC, Linux or Mac OS.

The hierarchical structure of subdirectories also correspond
to the default configuration of WrapScience, which uses distinct
directories by default for the different kinds of data.
Check the README.txt files in each of these directories.

Here is an outlook of the initial and default tree structure of
the WrapScience data storage system.
By default, the root is in  the "Images" directory in the user's home.

Images (at the root of the user's home directory)
└── wrapScience
    ├── README.txt
    ├── wrapGlobalMetaData
    │   ├── README.txt
    │   ├── wrapImaJ
    │   │   ├── predefined
    │   │   │   └── default__Calibration3D.wrapMdt
    │   │   └── README.txt
    │   └── wrapProcess
    │       ├── predefined
    │       │   └── default__NucleusVolumeThreshold.wrapMdt
    │       └── README.txt
    └── wrapResourceDir
        ├── README.txt
        └── wrapSampleData
            ├── default__NucleusVolumeThreshold.wrapMdt
            ├── Exp_5_DAPI_8bits.tif
            ├── Exp6_DAPI_8bits__Calibration3D.wrapMdt
            └── Exp6_DAPI_8bits.tif

