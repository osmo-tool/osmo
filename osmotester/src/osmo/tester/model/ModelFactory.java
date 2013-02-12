package osmo.tester.model;

import osmo.tester.parser.ModelObject;

import java.util.Collection;

/** @author Teemu Kanstren */
public interface ModelFactory {
  public Collection<ModelObject> createModelObjects();
}
