require xorg-app-common.inc
DESCRIPTION = "X Window System initializer"
DEPENDS += "util-linux-ng"
PE = "1"
PR = "${INC_PR}.0"

SRC_URI[archive.md5sum] = "56f2d202b3dc10fcd21931a67bb270f7"
SRC_URI[archive.sha256sum] = "16bcc73ae81b6d80fd1a9419ea757f38870d1043d440b6d56fcaa79d4c6c9b07"

FILES_${PN} += "${libdir}X11/xinit"