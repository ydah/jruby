/***** BEGIN LICENSE BLOCK *****
 * Version: EPL 2.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Eclipse Public
 * License Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/epl-v20.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2008 JRuby project
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the EPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the EPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/

package org.jruby.platform;

import org.jruby.util.SafePropertyAccessor;
import org.jruby.runtime.builtin.IRubyObject;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import org.jruby.runtime.Helpers;

/**
 * Platform specific constants.
 */
public abstract class Platform {

    private Platform() { }

    public String getPackageName() {
        return String.format("%s.%s.%s", Platform.class.getPackage().getName(), OS, ARCH);
    }
    public String getOSPackageName() {
        return String.format("%s.%s", Platform.class.getPackage().getName(), OS);
    }
    
    private static final String DARWIN = "darwin";
    private static final String WINDOWS = "windows";
    private static final String LINUX = "linux";
    private static final String FREEBSD = "freebsd";
    private static final String DRAGONFLYBSD = "dragonflybsd";
    private static final String OPENBSD = "openbsd";
    private static final String SOLARIS = "solaris";
    private static final String OPENVMS = "openvms";

    private static final String GCJ = "GNU libgcj";
    private static final String IBM = "IBM J9 VM";
    private static final String OPENJ9 = "Eclipse OpenJ9 VM";
    
    private static String initOperatingSystem() {
        String osname = getProperty("os.name", "unknown").toLowerCase();
        switch (osname) {
            case "mac os x" /* "Mac OS X" */ : return DARWIN;
        }
        if (osname.startsWith("windows")) {
            return WINDOWS;
        }
        if (osname.startsWith("sunos")) {
            return SOLARIS;
        }
        return osname;
    }

    private static String initArchitecture() {
        String arch = getProperty("os.arch", "unknown").toLowerCase();
        switch (arch) {
            case "x86" : return "i386";
            case "universal" : // OS X OpenJDK7 builds used to report "universal"
                String bits = SafePropertyAccessor.getProperty("sun.arch.data.model");
                if ("32".equals(bits)) {
                    System.setProperty("os.arch", "i386");
                    arch = "i386";
                } else if ("64".equals(bits)) {
                    System.setProperty("os.arch", "x86_64");
                    arch = "x86_64";
                }
        }
        return arch;
    }

    public static final String ARCH = initArchitecture();
    public static final String OS = initOperatingSystem();
    public static final String JVM = getProperty("java.vm.name", "unknown");
    public static final String OS_VERSION = getProperty("os.version", "unknown");

    public static final boolean IS_WINDOWS = OS.equals(WINDOWS);

    public static final boolean IS_MAC = OS.equals(DARWIN);
    public static final boolean IS_FREEBSD = OS.equals(FREEBSD);
    public static final boolean IS_DRAGONFLYBSD = OS.equals(DRAGONFLYBSD);
    public static final boolean IS_OPENBSD = OS.equals(OPENBSD);
    public static final boolean IS_LINUX = OS.equals(LINUX);
    public static final boolean IS_WSL = IS_LINUX && OS_VERSION.contains("Microsoft");
    public static final boolean IS_SOLARIS = OS.equals(SOLARIS);
    public static final boolean IS_BSD = IS_MAC || IS_FREEBSD || IS_OPENBSD || IS_DRAGONFLYBSD;
    public static final boolean IS_OPENVMS = OS.equals(OPENVMS);
    public static final String NAME = String.format("%s-%s", ARCH, OS);
    public static final int BIG_ENDIAN = 4321;
    public static final int LITTLE_ENDIAN = 1234;
    public static final int BYTE_ORDER = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN) ? BIG_ENDIAN : LITTLE_ENDIAN;

    public static final boolean IS_GCJ = JVM.equals(GCJ);
    public static final boolean IS_J9 = JVM.equals(OPENJ9) || JVM.equals(IBM);
    public static final boolean IS_IBM = IS_J9;

    /**
     * An extension over <code>System.getProperty</code> method.
     * Handles security restrictions, and returns the default
     * value if the access to the property is restricted.
     * @param property The system property name.
     * @param defValue The default value.
     * @return The value of the system property,
     *         or the default value.
     */
    public static String getProperty(String property, String defValue) {
        try {
            return System.getProperty(property, defValue);
        } catch (SecurityException se) {
            return defValue;
        }
    }

}
