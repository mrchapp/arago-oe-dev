require autofs.inc

SRC_URI = "${KERNELORG_MIRROR}/pub/linux/daemons/autofs/v4/autofs-${PV}.tar.bz2 \
           ${KERNELORG_MIRROR}/pub/linux/daemons/autofs/v4/autofs-4.1.4-misc-fixes.patch;patch=1 \
           ${KERNELORG_MIRROR}/pub/linux/daemons/autofs/v4/autofs-4.1.4-multi-parse-fix.patch;patch=1 \
           ${KERNELORG_MIRROR}/pub/linux/daemons/autofs/v4/autofs-4.1.4-non-replicated-ping.patch;patch=1 \
           file://cross.patch;patch=1 \
           file://Makefile.rules-cross.patch;patch=1 \
	   file://install.patch;patch=1 \
	   file://auto.net-sort-option-fix.patch;patch=1 \
	   file://autofs-additional-distros.patch;patch=1 \
	   file://no-bash.patch;patch=1"
PR = "${INC_PR}"

inherit update-rc.d

INITSCRIPT_NAME = "autofs"
INITSCRIPT_PARAMS = "defaults"

# FIXME: modules/Makefile has crappy rules that don't obey LDFLAGS
CFLAGS += "${LDFLAGS}"
EXTRA_OEMAKE = "STRIP=/bin/true"
PARALLEL_MAKE = ""