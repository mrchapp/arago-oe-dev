require glibc.inc

DEFAULT_PREFERENCE = "-1"
FILESDIR = "${@os.path.dirname(bb.data.getVar('FILE',d,1))}/eglibc-svn"
PV = "2.6+svn${SRCDATE}"
SRC_URI = "svn://svn.eglibc.org;module=trunk \
           file://export-fcntl2.patch;patch=1 \
           file://etc/ld.so.conf \
           file://generate-supported.mk"
S = "${WORKDIR}/trunk/libc"
B = "${WORKDIR}/build-${TARGET_SYS}"

#PACKAGES_DYNAMIC = "libc6*"
#RPROVIDES_${PN}-dev = "libc6-dev"

# the -isystem in bitbake.conf screws up glibc do_stage
BUILD_CPPFLAGS = "-I${STAGING_DIR}/${BUILD_SYS}/include"
TARGET_CPPFLAGS = "-I${STAGING_DIR}/${TARGET_SYS}/include"

GLIBC_ADDONS ?= "ports,nptl,libidn"

GLIBC_BROKEN_LOCALES = " _ER _ET so_ET yn_ER sid_ET tr_TR mn_MN gez_ET gez_ER bn_BD te_IN"

#
# For now, we will skip building of a gcc package if it is a uclibc one
# and our build is not a uclibc one, and we skip a glibc one if our build
# is a uclibc build.
#
# See the note in gcc/gcc_3.4.0.oe
#

python __anonymous () {
    import bb, re
    uc_os = (re.match('.*uclibc$', bb.data.getVar('TARGET_OS', d, 1)) != None)
    if uc_os:
        raise bb.parse.SkipPackage("incompatible with target %s" %
                                   bb.data.getVar('TARGET_OS', d, 1))
}

           
EXTRA_OECONF = "--enable-kernel=${OLDEST_KERNEL} \
                --without-cvs --disable-profile --disable-debug --without-gd \
                --enable-clocale=gnu \
                --enable-add-ons=${GLIBC_ADDONS} \
                --with-headers=${STAGING_INCDIR} \
                --without-selinux \
                ${GLIBC_EXTRA_OECONF}"

EXTRA_OECONF += "${@get_glibc_fpu_setting(bb, d)}"

do_configure_prepend() {
        if test -d ${WORKDIR}/trunk/ports ; then
	    mv ${WORKDIR}/trunk/ports ${S}/
	fi    
}

do_configure () {
# override this function to avoid the autoconf/automake/aclocal/autoheader
# calls for now
# don't pass CPPFLAGS into configure, since it upsets the kernel-headers
# version check and doesn't really help with anything
        if [ -z "`which rpcgen`" ]; then
                echo "rpcgen not found.  Install glibc-devel."
                exit 1
        fi
        (cd ${S} && gnu-configize) || die "failure in running gnu-configize"
        find ${WORKDIR} -name "configure" | xargs touch
        CPPFLAGS="" oe_runconf
}

rpcsvc = "bootparam_prot.x nlm_prot.x rstat.x \
          yppasswd.x klm_prot.x rex.x sm_inter.x mount.x \
          rusers.x spray.x nfs_prot.x rquota.x key_prot.x"

do_compile () { 
        # -Wl,-rpath-link <staging>/lib in LDFLAGS can cause breakage if another glibc is in staging
        unset LDFLAGS
	base_do_compile
	(
                cd ${S}/sunrpc/rpcsvc
                for r in ${rpcsvc}; do
                        h=`echo $r|sed -e's,\.x$,.h,'`
                        rpcgen -h $r -o $h || oewarn "unable to generate header for $r"
                done
        ) 
}       

do_stage() {
        rm -f ${STAGING_LIBDIR}/libc.so.6
        oe_runmake 'install_root=${STAGING_DIR}/${HOST_SYS}' \
                   'includedir=/include' 'libdir=/lib' 'slibdir=/lib' \
                   '${STAGING_LIBDIR}/libc.so.6' \
                   install-headers install-lib

        install -d ${STAGING_INCDIR}/gnu \
                   ${STAGING_INCDIR}/bits \
                   ${STAGING_INCDIR}/rpcsvc
        install -m 0644 ${S}/include/gnu/stubs.h ${STAGING_INCDIR}/gnu/
        install -m 0644 ${B}/bits/stdio_lim.h ${STAGING_INCDIR}/bits/
        install -m 0644 misc/syscall-list.h ${STAGING_INCDIR}/bits/syscall.h
        for r in ${rpcsvc}; do
                h=`echo $r|sed -e's,\.x$,.h,'`
                install -m 0644 ${S}/sunrpc/rpcsvc/$h ${STAGING_INCDIR}/rpcsvc/
        done
        for i in libc.a libc_pic.a libc_nonshared.a; do
                install -m 0644 ${B}/$i ${STAGING_LIBDIR}/ || die "failed to install $i"
        done
        echo 'GROUP ( libpthread.so.0 libpthread_nonshared.a )' > ${STAGING_LIBDIR}/libpthread.so
        echo 'GROUP ( libc.so.6 libc_nonshared.a )' > ${STAGING_LIBDIR}/libc.so

        rm -f ${CROSS_DIR}/${TARGET_SYS}/lib/libc.so.6
        oe_runmake 'install_root=${CROSS_DIR}/${TARGET_SYS}' \
                   'includedir=/include' 'libdir=/lib' 'slibdir=/lib' \
                   '${CROSS_DIR}/${TARGET_SYS}/lib/libc.so.6' \
                   install-headers install-lib

        install -d ${CROSS_DIR}/${TARGET_SYS}/include/gnu \
                   ${CROSS_DIR}/${TARGET_SYS}/include/bits \
                   ${CROSS_DIR}/${TARGET_SYS}/include/rpcsvc
        install -m 0644 ${S}/include/gnu/stubs.h ${CROSS_DIR}/${TARGET_SYS}/include/gnu/
        install -m 0644 ${B}/bits/stdio_lim.h ${CROSS_DIR}/${TARGET_SYS}/include/bits/
        install -m 0644 misc/syscall-list.h ${CROSS_DIR}/${TARGET_SYS}/include/bits/syscall.h
        for r in ${rpcsvc}; do
                h=`echo $r|sed -e's,\.x$,.h,'`
                install -m 0644 ${S}/sunrpc/rpcsvc/$h ${CROSS_DIR}/${TARGET_SYS}/include/rpcsvc/
        done

        for i in libc.a libc_pic.a libc_nonshared.a; do
                install -m 0644 ${B}/$i ${CROSS_DIR}/${TARGET_SYS}/lib/ || die "failed to install $i"
        done
        echo 'GROUP ( libpthread.so.0 libpthread_nonshared.a )' > ${CROSS_DIR}/${TARGET_SYS}/lib/libpthread.so
        echo 'GROUP ( libc.so.6 libc_nonshared.a )' > ${CROSS_DIR}/${TARGET_SYS}/lib/libc.so
}

require eglibc-package.bbclass