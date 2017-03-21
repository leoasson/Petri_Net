/*
 * Copyright (C) 2008-2010 Martin Riesz <riesz.martin at gmail.com>
 * Copyright (C) 2016-2017 Joaquín Rodríguez Felici <joaquinfelici at gmail.com>
 * Copyright (C) 2016-2017 Leandro Asson <leoasson at gmail.com>
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
package org.petrinator.editor.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.petrinator.util.GraphicsTools;
import org.petrinator.editor.Root;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class AboutAction extends AbstractAction {

    private Root root;

    public AboutAction(Root root) {
        this.root = root;
        String name = "About...";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/About16.gif"));
        putValue(SHORT_DESCRIPTION, name);
    }

    public void actionPerformed(ActionEvent e) {
        JOptionPane.showOptionDialog(
                root.getParentFrame(),  "                                                      " + "Universidad Nacional de Córdoba, Argentina\n" + 
                root.getAppLongName() + "\n" 
                + "\n"
                + "Authors: \n Joaquín Rodriguez Felici\n	Leandro Asson \n\n"
                + "Contributors:\n"
                + "Martin Riesz (PNEditor)\n"
                + "Milka Knaperekova (Boundedness algorithm)\n"
                + "JÃin TanÄibok (Reset and inhibitor arc types)\n"
                + "\n"
                + "This program is free software:  you can redistribute it and/or modify          \n"
                + "it under the terms of the GNU General Public License as published by\n"
                + "the Free Software Foundation, either version 3 of the License, or any\n"
                + "later version.\n"
                + "\n"
                + "This program is distributed in the hope that it will be useful, but with \n"
                + "no warranty or fitness for a particular purpose. See the GNU General \n"
                + "Public License for more details.\n\n"
                + "You should have received a copy of the GNU General Public License\n"
                + "along with this program. If not, see <http://www.gnu.org/licenses/>. \n\n",
                "About",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"OK"},
                "OK");
    }

}
