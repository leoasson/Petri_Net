package org.petrinator.monitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.petrinator.editor.PNEditor;
import org.petrinator.editor.Root;
import org.petrinator.editor.commands.FireTransitionCommand;
import org.petrinator.petrinet.PetriNet;
import org.petrinator.petrinet.*;

import rx.Observer;

/**
 * An observer who receives and stores string events.
 * Made for testing purposes
 *
 */
public class ConcreteObserver implements Observer<String> 
{

	/**
	 * A buffer for the recieved events
	 */
	private ArrayList<String> eventsRecieved;
	Root root;
	
	public ConcreteObserver(Root root) {
		super();
		this.root = root;
		eventsRecieved = new ArrayList<String>();
	}

	/**
	 * @see rx.Observer#onCompleted()
	 */
	@Override
	public void onCompleted() {
		eventsRecieved.add("COMPLETED");
	}

	/**
	 * @see rx.Observer#onError(java.lang.Throwable)
	 */
	@Override
	public void onError(Throwable t) {
		eventsRecieved.add("ERROR: " + t.getMessage() + " of type " + t.getClass().getName());
	}

	/**
	 * @brief This funcion is called everytime a transition is fired
	 * @see rx.Observer#onNext(java.lang.Object)
	 */
	@Override
	public void onNext(String event) 
	{
		eventsRecieved.add(event);
		
		/*
		 * Extract transition's ID from the JSON event.
		 */
		List<String> transitionInfo = Arrays.asList(event.split(","));
		String transitionId = transitionInfo.get(2);
		transitionId = transitionId.replace("\"","");
		transitionId = transitionId.replace("id:","");
		transitionId = transitionId.replace("}","");
		
		System.out.println(transitionId + " was fired!");
		
		/*
		 * Obtain transition object from the ID and the marking
		 */
		Transition transition = root.getDocument().petriNet.getRootSubnet().getTransition(transitionId);
		Marking marking = root.getDocument().petriNet.getInitialMarking();
		
		/*
		 * Graphically fire the transition 
		 */
		FireTransitionCommand fire = new FireTransitionCommand(transition,marking);
		fire.execute();
		root.refreshAll();
		
	}
	
	/**
	 * Getter for the events buffer
	 * @return the events recieved
	 */
	public ArrayList<String> getEvents(){
		return eventsRecieved;
	}

}