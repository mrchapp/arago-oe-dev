DESCRIPTION = "Neuros qt-plugins"
LICENSE = "GPL"

PV = "0.0+${PR}+gitr${SRCREV}"
PR = "r2"

DEPENDS = "qt-embedded"

inherit qtopia4core

SRCREV = "af9e7b73d0313046587cddaec5f879c78ce05d1a"
SRC_URI = "git://git.neurostechnology.com/git/lib-widgets;protocol=git"
S = "${WORKDIR}/git/"

do_install() {
	install -d ${D}/${libdir}/
	install -m 0755 ${S}/build/lib* ${D}/${libdir}
}

do_stage() {
	install -d ${STAGING_LIBDIR}
	install -m 0755 ${S}/build/lib* ${STAGING_LIBDIR}	
}