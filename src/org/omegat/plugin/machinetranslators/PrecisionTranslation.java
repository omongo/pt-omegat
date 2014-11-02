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

import org.omegat.core.machinetranslators.BaseTranslate;
import org.omegat.util.Language;

public class PrecisionTranslation extends BaseTranslate {

    public PrecisionTranslation() {
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
