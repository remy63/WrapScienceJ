This directory is the subdirectory of the wrapScience directory
structure for global metadata for the WrapScience Software Platform.
It contains metadata related to processes (rather than resources),
which are implemented using the WrapImaJ platform contained in the
WrapScience Platform.

A process is a piece of a program which can be executed to
perform algorithms with input resources and output resources.
A process can be launched in different ways, for example
by Graphical User Interface menu command (e.g. ImageJ plugin).
If the process requires parameters, a ".wrapMdt" file
can be used to initialize these parameters, either definitely,
of prior to an interactive request from the user.

Unless WrapScience has been specifically configured, this
directory, with the name "wrapProcess", is to be place in
the "wrapGlobalMetaData" directory (see the file README.txt in
the parent directory).
A description of the metadata file format (".wrapMdt" files)
and its usage can also be found in the file README.txt in the
parent directory.



