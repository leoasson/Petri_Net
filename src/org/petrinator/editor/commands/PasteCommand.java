package org.petrinator.editor.commands;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Set;

import org.petrinator.petrinet.Element;
import org.petrinator.petrinet.PetriNet;
import org.petrinator.petrinet.Subnet;
import org.petrinator.util.Command;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class PasteCommand implements Command {

    private Subnet subnet;
    private Set<Element> elements;
    private PetriNet petriNet;

    public PasteCommand(Set<Element> elements, Subnet currentSubnet, PetriNet petriNet) {
        this.subnet = currentSubnet;
        this.elements = elements;
        this.petriNet = petriNet;
        petriNet.getNodeLabelGenerator().setLabelsToPastedContent(elements);

        Point translation = calculateTranslatioToCenter(elements, currentSubnet);
        for (Element element : elements) {
            element.moveBy(translation.x, translation.y);
        }
    }

    public void execute() {
    	//TODO: Hacer un for para recorrer los elementos agregados y asignarles un nuevo ID. Sino, usa el mismo ID del original.
        subnet.addAll(elements);
    }

    public void undo() {
        subnet.removeAll(elements);
    }

    public void redo() {
        execute();
    }

    @Override
    public String toString() {
        return "Paste";
    }

    private Point calculateTranslatioToCenter(Set<Element> elements, Subnet currentSubnet) {
        Point viewTranslation = currentSubnet.getViewTranslation();
        Subnet tempSubnet = new Subnet();
        tempSubnet.addAll(elements);
        Rectangle bounds = tempSubnet.getBounds();

        Point result = new Point();
        result.translate(Math.round(-(float) bounds.getCenterX()), Math.round(-(float) bounds.getCenterY()));
        result.translate(-viewTranslation.x, -viewTranslation.y);
        return result;
    }

}
