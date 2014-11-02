/*
 * Copyright (C) 2014 Chanathip Srithanrat
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.omegat.plugin.machinetranslators;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JMenuItem;
import org.omegat.core.Core;
import org.omegat.core.machinetranslators.BaseTranslate;
import org.omegat.plugin.precisiontranslation.SettingsDialog;
import org.omegat.util.Language;

public class PrecisionTranslation extends BaseTranslate {

    public static final Preferences settings = Preferences.userNodeForPackage(PrecisionTranslation.class);

    public PrecisionTranslation() {

        JMenuItem item = new JMenuItem("Precision Translation Settings");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SettingsDialog(null, true).setVisible(true);
            }
        });
        Core.getMainWindow().getMainMenu().getOptionsMenu().add(item);

        File config = new File(System.getProperty("user.home"), ".precision-translation");
        if (!config.exists()) {
            settings.put("url", "http://74.208.75.116:62012/RPC2");
            settings.putBoolean("filter", true);
            try {
                config.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(PrecisionTranslation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public String getName() {
        return "Precision Translation";
    }

    @Override
    protected String getPreferenceName() {
        return "allow_precision_translation_plugin";
    }

    @Override
    protected String translate(Language sLang, Language tLang, String text) throws Exception {
        return "";
    }

}
