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
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JMenuItem;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.omegat.core.Core;
import org.omegat.core.machinetranslators.BaseTranslate;
import org.omegat.plugin.precisiontranslation.SettingsDialog;
import org.omegat.util.Language;

public class PrecisionTranslation extends BaseTranslate {

    static private long transUnitId;
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
            settings.put("engine", "translate-xliff");
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
    @SuppressWarnings("unchecked")
    protected String translate(Language sLang, Language tLang, String text) throws Exception {

        ++transUnitId;

        if (settings.getBoolean("filter", true)) {
            text = text.replaceAll("<\\/?[a-z]\\d+>", "");
        }

        text = StringEscapeUtils.escapeXml10(text);

        String transUnit = text;
        String xliff =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "<xliff version=\"1.2\">\n" +
            "  <file source-language=\"" + sLang + "\" target-language=\"" + tLang + "\">\n" +
            "    <header>\n" +
            "      <note from=\"PTTOOLS\">\n" +
            "        <graphname>" + settings.get("engine", null) + "</graphname>\n" +
            "      </note>\n" +
            "    </header>\n" +
            "    <body>\n" +
            "      <trans-unit id=\"" + Long.toString(transUnitId) + "\">\n" +
            "        <source>" + text + "</source>\n" +
            "      </trans-unit>\n" +
            "    </body>\n" +
            "  </file>\n" +
            "</xliff>\n";

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(settings.get("url", null)));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);

        try {
            Object[] params = new Object[] {new Object[] {xliff}};
            Object[] runResults = (Object[]) client.execute("run", params);
            String jobId = (String) runResults[0];

            if (jobId.equals("")) {
                return "ERROR!\n" + "Missing graph or invalid configurations";
            }

            params = new Object[] {new Object[] {jobId}, false, true, false};
            Map<String, Map> statusResults;
            String status;
            do {
                statusResults = (Map<String, Map>) client.execute("status", params);
                status = (String) statusResults.get(jobId).get("status");
            } while (!status.equals("completed") && !status.equals("failed"));

            if (status.equals("completed")) {
                Pattern p = Pattern.compile("state=\"new\">(.*)</target>", Pattern.MULTILINE);
                Matcher m = p.matcher((String) ((Object[]) (statusResults.get(jobId).get("content")))[0]);
                if (m.find()) {
                    transUnit = m.group(1);
                }
            }
        } catch (XmlRpcException ex) {
            Logger.getLogger(SettingsDialog.class.getName()).log(Level.WARNING, null, ex);
            transUnit = "ERROR!\n" + ex.getMessage();
        }

        return StringEscapeUtils.unescapeXml(transUnit);

    }

}
