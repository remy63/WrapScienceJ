This directory is the subdirectory of the default directory structure
for global metadata for the WrapScience Software Platform.

Unless WrapScience has been specifically configured, this
directory, with the name "wrapGlobalMetaData", is to be place in
the "wrapScience" directory (see the file README.txt in the
parent directory).

Metadata files contain parameters required by some processes
or ressources (datasets) used by the wrapScience Platform.
A wrapScience Metadata file hase extension ".wrapMdt", and
the first line contains the parameters, separated by a ";"
character. Each parameter has the form "Parameter name=value",where parameter name is a human readable defining string (which can be
used as a label of an input in a dialog box), and value
can be, depending on the type of the parameter, a numeric value
(e.g. 3.14116), or a string like "disbaled", or a string representation of a boolean ("true" or "false").

Each group of metadata parameters, called a "metadata set", is
to be placed in its own meatadata file, with a characteristic
basename postfix. The postfix is separated from the rest of
the basename by a double underscore "__" in the file name
(see .wrapMdt files in this subdirectories structure).
The basename postfix in a configuration file, which characterizes
the metadata set, must be a string with alphabetical
([a-z], [A-Z]) or numerical ([0-9]) characters only, without
any space or special character in it (see examples in the subdirectories).

A metadata set can be retrieved from ".wrapMdt" files either from a
default metadata directory (e.g. in a subdirectory of this one), or from a specific directory related to some process, or in the directory 
containing a related resource.
In the latter case, the basename -- before the last occurrence
of the separator "__" in the metadata file name -- can be
the basename of the resource file itself (if the metadata is to be 
managed on a resource by resource basis), or it can be "default"
(if the metadata is the same for all the resources immediately 
contained in that directory).
At last, of no specific configuration file or directory is specified, the metadata file is searched for in default global metadata 
directories, with a basename prefix "default__".

In other words, for each metadata set, a global default metadata file 
can be defined in a global metadata directory, and it can be
overridden in a specific directory (such as the directory containing
a related resource), or it can be overridden at the even more
specific level of a unique resource.



