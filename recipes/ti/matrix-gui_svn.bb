DESCRIPTION = "Matrix GUI for Qt X11"
HOMEPAGE = "https://gforge.ti.com/gf/project/matrix_gui/"
LICENSE = "BSD"
SECTION = "multimedia"
PRIORITY = "optional"

SRCREV = "136"
PV = "1.1"
PR = "r16+svnr${SRCPV}"

PLATFORM_dm365 = "dm365"
PLATFORM_da850-omapl138-evm = "omapl138"
PLATFORM_omap3evm = "omap3530"
PLATFORM_dm37x-evm = "dm3730"
PLATFORM_am37x-evm = "am3715"
PLATFORM_beagleboard = "am3715"
PLATFORM ?= "<UNDEFINED>"

SRC_URI = "svn://gforge.ti.com/svn/matrix_gui/;module=trunk;proto=https;user=anonymous;pswd='' "

S = "${WORKDIR}/trunk"

CXXFLAGS_da850-omapl138-evm_append = " -DPlatform_omapl138 "
CXXFLAGS_dm365_append = " -DPlatform_dm365 "
PACKAGE_ARCH = ${MACHINE_ARCH}

inherit qt4x11

do_install() {
	install -d ${D}/${bindir}
	install -m 0755 ${S}/matrix_gui ${D}/${bindir}
	install -d ${D}${sysconfdir}/init.d/
	install -c -m 0755 ${S}/${PLATFORM}/etc/x11/init ${D}${sysconfdir}/init.d/matrix-gui
	install -d ${D}/${sysconfdir}/xdg/autostart
	install -m 0755 ${S}/${PLATFORM}/etc/x11/matrix-gui.desktop ${D}/${sysconfdir}/xdg/autostart
}

FILES_${PN}-autostart = "${sysconfdir}/xdg/autostart/matrix-gui.desktop"

#Make autostart package depend on matrix-gui package.  Doesn't make sense
#to install the autostart package without the underlying matrix-gui package.
RDEPENDS_${PN}-autostart += "${PN}"
PACKAGES =+ "${PN}-autostart"

RDEPENDS_${PN} += "matrix-gui-common"