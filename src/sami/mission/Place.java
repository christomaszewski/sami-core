package sami.mission;

import sami.event.OutputEvent;
import sami.event.ReflectedEventSpecification;
import sami.gui.GuiConfig;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.logging.Logger;
import sami.markup.ReflectedMarkupSpecification;

/**
 *
 * @author pscerri
 */
public class Place extends Vertex {

    private static final Logger LOGGER = Logger.getLogger(Place.class.getName());
    static final long serialVersionUID = 5L;
    private boolean isStart, isEnd;
    private ArrayList<Transition> inTransitions = new ArrayList<Transition>();
    private ArrayList<Transition> outTransitions = new ArrayList<Transition>();
    private MissionPlanSpecification subMission = null;
    transient private boolean isActive = false;   // Whether this place has tokens and its output transition's input events are registered
    transient private boolean subMissionComplete = false;
    transient private ArrayList<OutputEvent> outputEvents = new ArrayList<OutputEvent>();
    transient private ArrayList<Token> tokens = new ArrayList<Token>();

    public Place(String name, FunctionMode functionMode) {
        super(name, functionMode);
    }

    public void addInTransition(Transition t) {
        if (inTransitions.contains(t)) {
            LOGGER.severe("Tried to add pre-existing inTransition: " + t);
            return;
        }
        inTransitions.add(t);
    }

    public void removeInTransition(Transition t) {
        if (!inTransitions.contains(t)) {
            LOGGER.severe("Tried to remove non-existing inTransition: " + t);
            return;
        }
        inTransitions.remove(t);
    }

    public void setIntransitions(ArrayList<Transition> inTransitions) {
        this.inTransitions = inTransitions;
    }

    public void setOutTransitions(ArrayList<Transition> outTransitions) {
        this.outTransitions = outTransitions;
    }

    public ArrayList<Transition> getInTransitions() {
        return inTransitions;
    }

    public void addOutTransition(Transition t) {
        if (outTransitions.contains(t)) {
            LOGGER.severe("Tried to add pre-existing outTransition: " + t);
            return;
        }
        outTransitions.add(t);
    }

    public void removeOutTransition(Transition t) {
        if (!outTransitions.contains(t)) {
            LOGGER.severe("Tried to remove non-existing outTransition: " + t);
            return;
        }
        outTransitions.remove(t);
    }

    public ArrayList<Transition> getOutTransitions() {
        return outTransitions;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setIsEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setIsStart(boolean isStart) {
        this.isStart = isStart;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        updateTag();
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean getSubMissionComplete() {
        return subMissionComplete;
    }

    public void setSubMissionComplete(boolean subMissionComplete) {
        this.subMissionComplete = subMissionComplete;
    }

    /**
     * Called when reading in a spec to run a mission, not when creating the
     * mission in the GUI
     *
     * @param e
     */
    public void addOutputEvent(OutputEvent e) {
        outputEvents.add(e);
    }

    public ArrayList<OutputEvent> getOutputEvents() {
        return outputEvents;
    }

    public MissionPlanSpecification getSubMission() {
        return subMission;
    }

    public void setSubMission(MissionPlanSpecification selectedMission) {
        subMission = selectedMission;
        updateTag();
    }

    public void addToken(Token token) {
        tokens.add(token);
        updateTag();
    }

    public boolean removeToken(Token token) {
        boolean success = tokens.remove(token);
        updateTag();
        return success;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public Shape getShape() {
        if (subMission == null) {
            return new Ellipse2D.Double(-10, -10, 20, 20);
        } else {
            return new Ellipse2D.Double(-15, -15, 30, 30);
        }
    }

    @Override
    public void updateTag() {
        tag = "<html>";
        shortTag = "<html>";
        if (GuiConfig.DRAW_LABELS && name != null && !name.equals("")) {
            tag += "<font color=" + GuiConfig.LABEL_TEXT_COLOR + ">" + name + "</font><br>";
            shortTag += "<font color=" + GuiConfig.LABEL_TEXT_COLOR + ">" + shorten(name, GuiConfig.MAX_STRING_LENGTH) + "</font><br>";
        }
        if (GuiConfig.DRAW_EVENTS) {
            for (ReflectedEventSpecification eventSpec : eventSpecs) {
                try {
                    Class eventClass = Class.forName(eventSpec.getClassName());
                    String simpleName = eventClass.getSimpleName();
                    if (OutputEvent.class.isAssignableFrom(eventClass)) {
                        tag += "<font color=" + GuiConfig.OUTPUT_EVENT_TEXT_COLOR + ">O:" + simpleName + "</font><br>";
                        shortTag += "<font color=" + GuiConfig.OUTPUT_EVENT_TEXT_COLOR + ">O:" + shorten(simpleName, GuiConfig.MAX_STRING_LENGTH) + "</font><br>";
                    } else {
                        continue;
                    }
                    if (GuiConfig.DRAW_MARKUPS) {
                        for (ReflectedMarkupSpecification markupSpec : eventSpec.getMarkupSpecs()) {
                            Class markupClass = Class.forName(markupSpec.getClassName());
                            tag += "<font color=" + GuiConfig.MARKUP_TEXT_COLOR + ">\tM: " + markupClass.getSimpleName() + "</font><br>";
                            shortTag += "<font color=" + GuiConfig.MARKUP_TEXT_COLOR + ">\tM:  " + shorten(markupClass.getSimpleName(), GuiConfig.MAX_STRING_LENGTH) + "</font><br>";
                        }
                    }
                } catch (ClassNotFoundException cnfe) {
                    cnfe.printStackTrace();
                }
            }
        }
        if (GuiConfig.DRAW_SUB_MISSIONS && subMission != null) {
            tag += "<font color=" + GuiConfig.SUB_MISSION_TEXT_COLOR + ">" + subMission.getName() + "</font><br>";
            shortTag += "<font color=" + GuiConfig.SUB_MISSION_TEXT_COLOR + ">" + shorten(subMission.getName(), GuiConfig.MAX_STRING_LENGTH) + "</font><br>";
        }
        if (GuiConfig.DRAW_TOKENS) {
            String tokenName;
            for (Token token : tokens) {
                tokenName = token.getName();
                tag += "<font color=" + GuiConfig.TOKEN_TEXT_COLOR + ">" + tokenName + "</font><br>";
                shortTag += "<font color=" + GuiConfig.TOKEN_TEXT_COLOR + ">" + shorten(tokenName, GuiConfig.MAX_STRING_LENGTH) + "</font><br>";
            }
        }
        tag += "</html>";
        shortTag += "</html>";
    }

    @Override
    public String toString() {
        return "Place:" + name;
    }

    public Place copyWithoutConnections() {
        Place copy = new Place(name, functionMode);
        copy.visibilityMode = visibilityMode;
        for (ReflectedEventSpecification eventSpec : eventSpecs) {
            copy.eventSpecs.add(eventSpec.copy());
        }
        copy.isStart = isStart;
        copy.isEnd = isEnd;
        copy.isActive = isActive;
        if (subMission != null) {
            // @todo: Should we be doing a copy with a prefix addition?
            copy.subMission = new MissionPlanSpecification(subMission);
        }
        copy.updateTag();
        return copy;
    }

    private void readObject(ObjectInputStream ois) {
        try {
            ois.defaultReadObject();
            outputEvents = new ArrayList<OutputEvent>();
            tokens = new ArrayList<Token>();
            updateTag();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
