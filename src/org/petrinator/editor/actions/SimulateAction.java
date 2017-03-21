package org.petrinator.editor.actions;

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

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import rx.Observer;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.petrinator.editor.filechooser.FileChooserDialog;
import org.petrinator.editor.filechooser.FileType;
import org.petrinator.editor.filechooser.FileTypeException;
import org.petrinator.monitor.ConcreteObserver;
import org.petrinator.util.GraphicsTools;
import org.petrinator.util.StringTools;
import org.petrinator.editor.Root;
import org.unc.lac.javapetriconcurrencymonitor.errors.IllegalTransitionFiringError;
import org.unc.lac.javapetriconcurrencymonitor.exceptions.PetriNetException;
import org.unc.lac.javapetriconcurrencymonitor.monitor.PetriMonitor;
import org.unc.lac.javapetriconcurrencymonitor.monitor.policies.FirstInLinePolicy;
import org.unc.lac.javapetriconcurrencymonitor.monitor.policies.TransitionsPolicy;
import org.unc.lac.javapetriconcurrencymonitor.petrinets.RootPetriNet;
import org.unc.lac.javapetriconcurrencymonitor.petrinets.components.Transition;
import org.unc.lac.javapetriconcurrencymonitor.petrinets.factory.PetriNetFactory;
import org.unc.lac.javapetriconcurrencymonitor.petrinets.factory.PetriNetFactory.petriNetType;

import rx.Subscription;

/**
 * @author Joaquin Felici <joaquinfelici at gmail.com>
 * @brief When button is pressed, it creates a temp.pnml file in petrinator/temp folder.
 * @detail Once it's finished, it's supposed to start to call the monitor, start the 
 * threads, and subscribe to trasitions.
 * TODO: change file format in root (line 457)
 */
public class SimulateAction extends AbstractAction {

	private Root root;
    private List<FileType> fileTypes;

    public SimulateAction(Root root, List<FileType> fileTypes) {
        this.root = root;
        this.fileTypes = fileTypes;
        String name = "Simulation";
        putValue(NAME, name);
        putValue(SMALL_ICON, GraphicsTools.getIcon("pneditor/play16.gif"));
        putValue(SHORT_DESCRIPTION, name);
    }

    public void actionPerformed(ActionEvent e) 
    {
        FileChooserDialog chooser = new FileChooserDialog();

        if (root.getCurrentFile() != null) 
        {
            chooser.setSelectedFile(root.getCurrentFile());
        }

        for (FileType fileType : fileTypes) 
        {
            chooser.addChoosableFileFilter(fileType);
        }
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setCurrentDirectory(root.getCurrentDirectory());
        chooser.setDialogTitle("Save as...");

        File file = new File("temp/" + "temp" + "." + "pnml");
        FileType chosenFileType = (FileType) chooser.getFileFilter();
        try 
        {
        	chosenFileType.save(root.getDocument(), file);
        } 
        catch (FileTypeException e1) 
        {
        	e1.printStackTrace();
        }
        
        createMonitor();
    }
    
    public void createMonitor()
    {
    	 PetriNetFactory factory = new PetriNetFactory("temp/temp.pnml");
		 RootPetriNet petri = factory.makePetriNet(petriNetType.PLACE_TRANSITION);
		 TransitionsPolicy policy = new FirstInLinePolicy();
		 PetriMonitor monitor = new PetriMonitor(petri, policy);
		 
		 petri.initializePetriNet();
		 /*
		  * Subscribe to all transitions
		  */
		 Observer<String> observer = new ConcreteObserver(root);
		 for(int i = 0; i < petri.getTransitions().length; i++)
		 {
			 Transition t = petri.getTransitions()[i];
			 Subscription subscription = monitor.subscribeToTransition(t, observer); 
		 }
		 
		 //createThread(monitor, petri.getTransitions());
		 List<Thread> threads = new ArrayList<Thread>();
		 for(int i = 0; i < petri.getTransitions().length; i++)
		 {
			Thread t = createThread(monitor, petri.getTransitions()[i].getId());
			threads.add(t);
			t.start();
		 }
		 
		/* 
		 Integer [] marking = petri.getCurrentMarking();
	     for(int i = 0; i < marking.length; i++)
	    	 System.out.print(marking[i] + "  ");
	    */
    }
    
   void fireTransition(PetriMonitor m, String id)
    {
    	try 
		 {
			m.fireTransition(id);
		 }
		 catch (IllegalArgumentException | IllegalTransitionFiringError | PetriNetException e) 
		 {
			e.printStackTrace();
		}
    }
    
    Thread createThread(PetriMonitor m, String id)
    {
    	Thread t = new Thread(new Runnable() {
			  @Override
			  public void run() 
			  {
				while(true)
				{
					try 
				    {	Thread.currentThread().join();
				    	m.fireTransition(id);	
				    } catch (IllegalTransitionFiringError | IllegalArgumentException | PetriNetException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			  }
			});
			return t;
    }
 
}