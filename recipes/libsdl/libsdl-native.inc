DESCRIPTION = "Simple DirectMedia Layer - native Edition"
HOMEPAGE = "http://www.libsdl.org"
SECTION = "libs"
LICENSE = "LGPL"
INC_PR = "r3"

SRC_URI = "http://www.libsdl.org/release/SDL-${PV}.tar.gz \
"

S = "${WORKDIR}/SDL-${PV}"

inherit autotools binconfig pkgconfig native

PARALLEL_MAKE = ""

EXTRA_OECONF = "--disable-debug --disable-cdrom --enable-threads --enable-timers --enable-endian \
                --enable-file --disable-oss --disable-alsa --disable-esd --disable-arts \
                --disable-diskaudio --disable-nas --disable-esd-shared --disable-esdtest \
                --disable-mintaudio --disable-nasm --disable-video-x11 --disable-video-dga \
                --disable-video-fbcon --disable-video-directfb --disable-video-ps2gs \
                --disable-video-xbios --disable-video-gem --disable-video-dummy \
                --disable-video-opengl --enable-input-events --enable-pthreads \
                --disable-video-picogui --disable-video-qtopia --enable-dlopen"

do_configure() {
        gnu-configize
        oe_runconf
        cd ${S}
        # prevent libtool from linking libs against libstdc++, libgcc, ...
        cat ${HOST_SYS}-libtool | sed -e 's/postdeps=".*"/postdeps=""/' > ${HOST_SYS}-libtool.tmp
        mv ${HOST_SYS}-libtool.tmp ${HOST_SYS}-libtool
}

