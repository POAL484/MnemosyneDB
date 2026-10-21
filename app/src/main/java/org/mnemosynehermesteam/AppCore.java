package org.mnemosynehermesteam;

import org.mnemosynehermesteam.core.Core;

public class AppCore {
    
    static final Core core = new Core();

    public static Core getCore() {
        return core;
    }

}
