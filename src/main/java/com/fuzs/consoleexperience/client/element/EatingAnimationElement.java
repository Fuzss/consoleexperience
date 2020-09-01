package com.fuzs.consoleexperience.client.element;

public class EatingAnimationElement extends GameplayElement {

    @Override
    public void setup() {

    }

    @Override
    protected boolean getDefaultState() {

        return true;
    }

    @Override
    protected String getDisplayName() {

        return "Eating Animation";
    }

    @Override
    protected String getDescription() {

        return "Animate eating in third-person view.";
    }

}
