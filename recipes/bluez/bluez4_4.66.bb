require bluez4.inc

SRC_URI[md5sum] = "8f8ab0b35a38ef5238c83d2547c149ad"
SRC_URI[sha256sum] = "b0857f946dd1437b12847dfc3283b24f3be97290be9021db242ffece6a454fe3"

DEFAULT_PREFERENCE = "-1"

DEPENDS += "libnl libsndfile1"

PR = "${INC_PR}.1"

# Not all distros have a recent enough udev
BTUDEV = " --disable-udevrules"
BTUDEV_angstrom = " --enable-udevrules"
BTUDEV_shr = " --enable-udevrules"
EXTRA_OECONF += "${BTUDEV}"
