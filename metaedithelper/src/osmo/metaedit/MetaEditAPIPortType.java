/**
 * MetaEditAPIPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package osmo.metaedit;

public interface MetaEditAPIPortType extends java.rmi.Remote {

    /**
     * Returns a set of all instances of this Graph type in currently
     * open projects.
     */
    public osmo.metaedit.MEOop[] allGoodInstances(osmo.metaedit.METype receiver) throws java.rmi.RemoteException;

    /**
     * Returns a set of all instances of this Graph type or its subtypes
     * in currently open projects.
     */
    public osmo.metaedit.MEOop[] allSimilarInstances(osmo.metaedit.METype receiver) throws java.rmi.RemoteException;

    /**
     * Looks at subtypes of the METype receiver argument, and returns
     * the METype of the first subtype whose name matches the wildcard string
     * in the second argument. If none are found, returns the same METype
     * as the receiver argument.
     */
    public osmo.metaedit.METype subTypeNamed(osmo.metaedit.METype receiver, java.lang.String subTypeNamed) throws java.rmi.RemoteException;

    /**
     * Returns the set of NonProperties from this Graph whose id property
     * value matches the specified string, which can contain ? and * as wildcards.
     * Case insensitive.
     */
    public osmo.metaedit.MEOop[] findString(osmo.metaedit.MEOop receiver, java.lang.String findString) throws java.rmi.RemoteException;

    /**
     * Returns a sorted collection of all loaded matching instances
     * of matching types. Types may be the METype receiver argument or its
     * subtypes. Matching is case insensitive with wildcards. Note that only
     * graph instances in open projects are guaranteed to be loaded.
     */
    public osmo.metaedit.MEOop[] instancesNamed(osmo.metaedit.METype receiver, java.lang.String instancesNamed, java.lang.String ofSubTypesNamed) throws java.rmi.RemoteException;

    /**
     * Returns the user-visible string name of the receiver argument,
     * an METype.
     */
    public java.lang.String typeName(osmo.metaedit.METype receiver) throws java.rmi.RemoteException;

    /**
     * Returns an METype, the type of the receiver argument. The receiver
     * argument must be the MEOop of a GOPRR instance.
     */
    public osmo.metaedit.METype type(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns the simple string representation of the receiver argument,
     * an MEOop representing any conceptual or representational element.
     */
    public java.lang.String userPrintString(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns an MEAny representing the nth property value of the
     * receiver argument, a NonProperty MEOop. The second argument must be
     * an integer, starting with 1 for the first property.
     */
    public osmo.metaedit.MEAny valueAt(osmo.metaedit.MEOop receiver, int valueAt) throws java.rmi.RemoteException;

    /**
     * Sets the value of the the nth property value of the receiver
     * argument, a NonProperty MEOop. The second argument must be an integer,
     * starting with 1 for the first property. The third argument is an MEAny
     * containing the value for the property. Returns an MEAny containing
     * the MEOop of the property that was changed, or an MEAny containing
     * an MENull if unsuccessful (e.g. if the value is not of the right type
     * or fails the property type's regular expression rule).
     */
    public osmo.metaedit.MEAny setValueAt(osmo.metaedit.MEOop receiver, int valueAt, osmo.metaedit.MEAny put) throws java.rmi.RemoteException;

    /**
     * Returns an MEAnyArray of the values of the properties of the
     * receiver argument, a NonProperty MEOop.
     */
    public osmo.metaedit.MEAny[] allPropertiesValues(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns an MEOopArray of the properties of the receiver argument,
     * a NonProperty MEOop. (Note this returns the actual properties, not
     * their values: if you want the values, use allPropertiesValues.) This
     * can be useful when working with property sharing; note though that
     * instProps requires an MEAnyArray, so each MEOop returned here would
     * have to be wrapped in an MEAny. The method allPropertiesWrapped returns
     * such an MEAnyArray.
     */
    public osmo.metaedit.MEOop[] allProperties(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns an MEAnyArray of the properties of the receiver argument,
     * a NonProperty MEOop. (Note this returns the actual properties, not
     * their values.) This is useful for setting up property sharing, since
     * instProps requires an MEAnyArray of properties. To get just the properties
     * as an MEOopArray, use allProperties. To get just their values, use
     * allPropertiesValues.
     */
    public osmo.metaedit.MEAny[] allPropertiesWrapped(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Creates or sets the properties of an instance of the METype
     * argument, returning an MEAny with the MEOop of the instance, or MENull
     * for an error. The first argument is the METype; propColl is an array
     * of MEAnys, either MEOops of existing property instances or MENull
     * to create a new property of the default type; valueColl is an array
     * of MEAnys, the values of the properties; np is an MEAny, either the
     * MEOop of an existing NonProperty of this type, or MENull to create
     * a new one; inArea is an MEAny, either an integer corresponding to
     * the project's area number, or MENull for the default (the same project
     * as np if it exists already, else the current default project).
     */
    public osmo.metaedit.MEAny instProps(osmo.metaedit.METype receiver, osmo.metaedit.MEAny[] propColl, osmo.metaedit.MEAny[] valueColl, osmo.metaedit.MEAny np, osmo.metaedit.MEAny inArea) throws java.rmi.RemoteException;

    /**
     * Create and return the MEOop of a new instance of the receiver
     * argument, a NonProperty type METype. The result is created in the
     * current default project. Properties are created with their default
     * values, and the result is not checked against the metamodel rules.
     */
    public osmo.metaedit.MEOop unsafeNew(osmo.metaedit.METype receiver) throws java.rmi.RemoteException;

    /**
     * Returns the set of all Objects in this Graph
     */
    public osmo.metaedit.MEOop[] objectSet(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns the set of all Relationships in this Graph.
     */
    public osmo.metaedit.MEOop[] relationshipSet(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns the set of all Roles in this Graph.
     */
    public osmo.metaedit.MEOop[] roleSet(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns the set of all Binding MEOops in this Graph MEOop
     */
    public osmo.metaedit.MEOop[] bindingSet(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns the set of Graphs that are explosions of objects in
     * the receiver argument, a Graph MEOop.
     */
    public osmo.metaedit.MEOop[] explodeGraphs(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns the set of Graphs that are decompositions of objects
     * in the receiver argument, a Graph MEOop.
     */
    public osmo.metaedit.MEOop[] decompGraphs(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns the set of Graphs that are subgraphs (i.e. either explosions
     * or decompositions) of the receiver argument, a Graph MEOop.
     */
    public osmo.metaedit.MEOop[] subgraphs(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * In the receiver argument, a Graph MEOop, add a new explosion
     * to the second argument, a Graph MEOop, for the final argument, an
     * Object MEOop.
     */
    public osmo.metaedit.MEOop addExplode(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop addExplode, osmo.metaedit.MEOop _for) throws java.rmi.RemoteException;

    /**
     * For this Graph and this NonProperty, returns the set of explosion
     * subgraphs
     */
    public osmo.metaedit.MEOop[] getExplodeGraphsForNP(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop getExplodeGraphsForNP) throws java.rmi.RemoteException;

    /**
     * Removes the explosion link from the receiver argument, a Graph
     * MEOop. The second argument is the target Graph MEOop of the explosion
     * to remove. The third argument is the source Object, Role or Relationship
     * MEOop of the explosion to remove. The result is the receiver Graph
     * MEOop.
     */
    public osmo.metaedit.MEOop removeExplode(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop removeExplode, osmo.metaedit.MEOop _for) throws java.rmi.RemoteException;

    /**
     * Get the decomposition of the receiver argument, an Object MEOop.
     * Returns an MEAny containing either a Graph MEOop or MENull.
     */
    public osmo.metaedit.MEAny decompGraph(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Set the decomposition of the receiver argument, an Object MEOop,
     * to be the decompGraph argument, an MEAny containing either a Graph
     * MEOop or MENull. Returns an MEAny containing either the receiver for
     * success or MENull for failure.
     */
    public osmo.metaedit.MEAny setDecompGraph(osmo.metaedit.MEOop receiver, osmo.metaedit.MEAny decompGraph) throws java.rmi.RemoteException;

    /**
     * For this Graph and this Object, returns the set of Objects
     * directly connected to it. The third argument restricts the type of
     * the connected Objects: use NonProperty for no restriction.
     */
    public osmo.metaedit.MEOop[] objsForObj(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop objsForObj, osmo.metaedit.METype ofClass) throws java.rmi.RemoteException;

    /**
     * For this Graph and this Relationship, returns the set of Objects
     * directly connected to it. The third argument restricts the type of
     * the connected Objects: use NonProperty for no restriction.
     */
    public osmo.metaedit.MEOop[] objsForRel(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop objsForRel, osmo.metaedit.METype ofClass) throws java.rmi.RemoteException;

    /**
     * For this Graph and this Role, returns the set of Objects directly
     * connected to it. The third argument restricts the type of the connected
     * Objects: use NonProperty for no restriction.
     */
    public osmo.metaedit.MEOop[] objsForRole(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop objsForRole, osmo.metaedit.METype ofClass) throws java.rmi.RemoteException;

    /**
     * For this Graph and this Object, returns the set of Relationships
     * directly connected to it. The third argument restricts the type of
     * the connected Relationships: use NonProperty for no restriction.
     */
    public osmo.metaedit.MEOop[] relsForObj(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop relsForObj, osmo.metaedit.METype ofClass) throws java.rmi.RemoteException;

    /**
     * For this Graph and this Role, returns the set of Relationships
     * directly connected to it. The third argument restricts the type of
     * the connected Relationships: use NonProperty for no restriction.
     */
    public osmo.metaedit.MEOop[] relsForRole(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop relsForRole, osmo.metaedit.METype ofClass) throws java.rmi.RemoteException;

    /**
     * For this Graph and this Object, returns the set of Roles directly
     * connected to it. The third argument restricts the type of the connected
     * Roles: use NonProperty for no restriction.
     */
    public osmo.metaedit.MEOop[] rolesForObj(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop rolesForObj, osmo.metaedit.METype ofClass) throws java.rmi.RemoteException;

    /**
     * For this Graph and this Relationship, returns the set of Roles
     * directly connected to it. The third argument restricts the type of
     * the connected Roles: use NonProperty for no restriction.
     */
    public osmo.metaedit.MEOop[] rolesForRel(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop rolesForRel, osmo.metaedit.METype ofClass) throws java.rmi.RemoteException;

    /**
     * For this Graph and this Roles, returns the set of Roles that
     * belong to the same binding as the referred Role. The third argument
     * restricts the type of the connected Roles: use NonProperty for no
     * restriction.
     */
    public osmo.metaedit.MEOop[] rolesForRole(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop rolesForRole, osmo.metaedit.METype ofClass) throws java.rmi.RemoteException;

    /**
     * Opens the receiver argument in a graph representation. Will
     * prompt with a list dialog in MetaEdit+ if there is more than one possibility.
     */
    public osmo.metaedit.MENull open(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Opens the specified graph and highlights the specified NonProperty.
     * The receiver argument is the MEOop of the graph, the second is the
     * MEOop of the NonProperty. This will never prompt.
     */
    public osmo.metaedit.MENull animate(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop animate) throws java.rmi.RemoteException;

    /**
     * Refreshes the display of the receiver argument, which is an
     * MEOop of any conceptual or representational instance.
     */
    public osmo.metaedit.MENull refresh(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns the MEOops of objects currently in the copy buffer.
     * Bindings in the copy buffer are ignored. This is a useful way of allowing
     * users to select an object in MetaEdit+, and have your application
     * know which object was selected: rather than just selecting the object,
     * they can select and copy it.
     */
    public osmo.metaedit.MEOop[] copiedObjects(osmo.metaedit.MENull receiver) throws java.rmi.RemoteException;

    /**
     * Delete the receiver argument, a Graph MEOop, and all its representations.
     * Note this does not delete links from other graphs etc. to this graph.
     */
    public osmo.metaedit.MENull delete(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Adds the receiver argument, an Object MEOop, to the second
     * argument, a Graph MEOop. No type checking is performed.
     */
    public osmo.metaedit.MENull addToGraph(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop addToGraph) throws java.rmi.RemoteException;

    /**
     * Creates a new binding into the receiver argument, a Graph MEOop.
     * The second argument is the METype of the relationship to create. The
     * third argument is an METypeArray of the types of Roles to create.
     * The final argument is an MEOopArray of the existing Objects that this
     * binding connects, in the same order as the third argument. The return
     * value is the newly created binding MEOop.
     */
    public osmo.metaedit.MEOop createBinding(osmo.metaedit.MEOop receiver, osmo.metaedit.METype relType, osmo.metaedit.METype[] roleTypes, osmo.metaedit.MEOop[] objects) throws java.rmi.RemoteException;

    /**
     * Returns the relationship MEOop of this binding MEOop
     */
    public osmo.metaedit.MEOop relationship(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns an MEOopArray of the roles in this binding MEOop in
     * order
     */
    public osmo.metaedit.MEOop[] roles(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns an MEOopArray of the objects in this binding MEOop
     * in order
     */
    public osmo.metaedit.MEOop[] objects(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns an MEOopArray containing the set of diagram representations
     * of the receiver argument, a Graph MEOop.
     */
    public osmo.metaedit.MEOop[] diagrams(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns an MEOopArray containing the set of matrix representations
     * of the receiver argument, a Graph MEOop.
     */
    public osmo.metaedit.MEOop[] matrices(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns an MEOopArray containing the set of table representations
     * of the receiver argument, a Graph MEOop.
     */
    public osmo.metaedit.MEOop[] tables(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Creates a new diagram representation for the receiver argument,
     * a Graph MEOop, and returns the MEOop for the new representation.
     */
    public osmo.metaedit.MEOop newDiagram(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Creates a new matrix representation for the receiver argument,
     * a Graph MEOop, and returns the MEOop for the new representation.
     */
    public osmo.metaedit.MEOop newMatrix(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Creates a new table representation for the receiver argument,
     * a Graph MEOop, and returns the MEOop for the new representation.
     */
    public osmo.metaedit.MEOop newTable(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns the NonProperty MEOop represented by the receiver argument,
     * an MEOop of a representation element.
     */
    public osmo.metaedit.MEOop inst(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns an MEOopArray containing all representation elements
     * of the receiver argument, a NonProperty MEOop.
     */
    public osmo.metaedit.MEOop[] repSet(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns the representation graph to which this representation
     * element belongs. The receiver argument must be an MEOop of a representation
     * element.
     */
    public osmo.metaedit.MEOop reprGraph(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Removes the receiver argument, any representation MEOop. Returns
     * the NonProperty MEOop the receiver used to represent.
     */
    public osmo.metaedit.MEOop remove(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns the timestamp of the receiver argument, a representation
     * graph MEOop.
     */
    public java.util.Calendar timeStamp(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Sets the timestamp of the receiver argument, a representation
     * graph MEOop.
     */
    public osmo.metaedit.MENull setTimeStamp(osmo.metaedit.MEOop receiver, java.util.Calendar timeStamp) throws java.rmi.RemoteException;

    /**
     * Returns an MEOopArray of the object representations in the
     * receiver argument, a diagram MEOop. The result is in Z-order, backmost
     * first.
     */
    public osmo.metaedit.MEOop[] objectReprs(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns an MEOopArray of the binding representations in the
     * receiver argument, a diagram MEOop. The result is in Z-order, backmost
     * first.
     */
    public osmo.metaedit.MEOop[] bindingReprs(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns an MEAny containing a point specifying the size of
     * the grid in the receiver argument, a diagram MEOop.
     */
    public osmo.metaedit.MEAny grid(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Sets the size of the grid in the receiver argument, a diagram
     * MEOop. The value is an MEAny containing a point specifying the size
     * of the grid.
     */
    public osmo.metaedit.MENull setGrid(osmo.metaedit.MEOop receiver, osmo.metaedit.MEAny grid) throws java.rmi.RemoteException;

    /**
     * Returns a Boolean indicating whether the grid is displayed
     * in the receiver argument, a diagram MEOop.
     */
    public boolean displayGrid(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Sets a Boolean indicating whether the grid is displayed in
     * the receiver argument, a diagram MEOop.
     */
    public osmo.metaedit.MENull setDisplayGrid(osmo.metaedit.MEOop receiver, boolean displayGrid) throws java.rmi.RemoteException;

    /**
     * Returns a Boolean indicating whether Snap to Grid is on in
     * the receiver argument, a diagram MEOop.
     */
    public boolean useGrid(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Sets a Boolean indicating whether Snap to Grid is on in the
     * receiver argument, a diagram MEOop.
     */
    public osmo.metaedit.MENull setUseGrid(osmo.metaedit.MEOop receiver, boolean useGrid) throws java.rmi.RemoteException;

    /**
     * Returns the position of the receiver, an MEOop for an object
     * or binding representation in a diagram argument. The result returned
     * is an MEAny containing a point, where 1 represents 1 pixel at 100%
     * zoom.
     */
    public osmo.metaedit.MEAny place(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Sets the position of the receiver argument, an MEOop for an
     * object or binding representation in a diagram. The value is an MEAny
     * containing a point, where 1 represents 1 pixel at 100% zoom.
     */
    public osmo.metaedit.MENull setPlace(osmo.metaedit.MEOop receiver, osmo.metaedit.MEAny place) throws java.rmi.RemoteException;

    /**
     * Returns the scale of the receiver argument, an MEOop for an
     * object or binding representation in a diagram. The result returned
     * is an MEAny containing a point, where 1 represents 100%.
     */
    public osmo.metaedit.MEAny scale(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Sets the scale of the receiver argument, an MEOop for an object
     * or binding representation in a diagram. The value is an MEAny containing
     * a point, where 1 represents 100%.
     */
    public osmo.metaedit.MENull setScale(osmo.metaedit.MEOop receiver, osmo.metaedit.MEAny scale) throws java.rmi.RemoteException;

    /**
     * Returns a Boolean indicating whether the receiver argument,
     * a diagram binding representation MEOop, is straight.
     */
    public boolean isStraight(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Straightens the receiver argument, a diagram binding representation
     * MEOop. The receiver must be a binary binding. All breakpoints and
     * target point offsets are removed from all connection representations,
     * and the relationship position is set to the midpoint of the role lines.
     */
    public osmo.metaedit.MENull straighten(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Creates a new diagram representation with automatic layout
     * for the graph denoted by the first MEOop argument. The auto-layout
     * algorithm assumes a directed acyclic graph (DAG) as its input and
     * therefore the user must provide the API call with rules according
     * to which the DAG is built. Therefore, the following argument, relationship,
     * defines the relationship type which instances appear as edges in the
     * DAG. The two following arguments, parentRole and childRole, define
     * the directivity according to which the DAG is build. The fifth argument,
     * direction, provides the information how the layout is distributed
     * and can have either 'vertical' or 'horizontal' as values. The final
     * argument, manhattan, toggles the usage of orthogonal or diagonal lines
     * between DAG nodes.
     */
    public osmo.metaedit.MENull layout(osmo.metaedit.MEOop receiver, osmo.metaedit.METype layoutRelationship, osmo.metaedit.METype fromRole, osmo.metaedit.METype toRole, java.lang.String direction, boolean manhattan) throws java.rmi.RemoteException;

    /**
     * Adds a new object representation to the receiver argument,
     * a diagram MEOop. The second argument is the MEOop of the object to
     * represent. The third argument is a 1-based integer before which to
     * insert the new binding representation in the Z-ordered collection
     * of binding representations: 1 is the backmost element. The fourth
     * argument is an MEAny containing the point position of the new object
     * representation. The return value is the MEOop of the new object representation.
     */
    public osmo.metaedit.MEOop addNewObjectRepFor(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop addNewObjectRepFor, int beforeIndex, osmo.metaedit.MEAny place) throws java.rmi.RemoteException;

    /**
     * Adds a new binding representation to the receiver argument,
     * a diagram MEOop. The second argument is the MEOop of the binding to
     * represent. The third argument is a 1-based integer before which to
     * insert the new binding representation in the Z-ordered collection
     * of binding representations: 1 is the backmost element. The fourth
     * argument is an MEAny containg the point position of the relationship
     * representation. The return value is the MEOop of the new binding representation.
     */
    public osmo.metaedit.MEOop addNewBindingRepFor(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop addNewBindingRepFor, int beforeIndex, osmo.metaedit.MEAny place) throws java.rmi.RemoteException;

    /**
     * Returns an MEAnyArray containing the integer ids of the connections
     * represented in the receiver argument, a diagram binding representation
     * MEOop.
     */
    public osmo.metaedit.MEAny[] connectionReprIds(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Adds a new breakpoint to a role representation in the receiver
     * argument, a diagram binding representation MEOop. The second argument
     * is an MEAny containing the breakpoint coordinate. The third argument
     * is the 1-based integer index before which to insert the new breakpoint,
     * counting from the object. The fourth argument is the integer id of
     * the connection within this binding.
     */
    public osmo.metaedit.MENull addPoint(osmo.metaedit.MEOop receiver, osmo.metaedit.MEAny addPoint, int index, int id) throws java.rmi.RemoteException;

    /**
     * Moves the position of a breakpoint in a role representation
     * in the receiver argument, a diagram binding representation MEOop.
     * The second argument is the 1-based integer index of the breakpoint,
     * counting from the object. The third argument is an MEAny containing
     * the new coordinate for the breakpoint. The fourth argument is the
     * integer id of the connection within this binding.
     */
    public osmo.metaedit.MENull putPointAtIndex(osmo.metaedit.MEOop receiver, int at, osmo.metaedit.MEAny putPoint, int id) throws java.rmi.RemoteException;

    /**
     * Sets the breakpoints of a role representation in the receiver
     * argument, a diagram binding representation MEOop. The second argument
     * is an MEAnyArray containing the coordinates of the breakpoints. The
     * third argument is the integer id of the connection within this binding.
     */
    public osmo.metaedit.MENull breakpoints(osmo.metaedit.MEOop receiver, osmo.metaedit.MEAny[] breakpoints, int id) throws java.rmi.RemoteException;

    /**
     * Sets the target point offset for a role representation in the
     * receiver argument, a diagram binding representation MEOop. The second
     * argument is an MEAny containing the offset coordinate. The third argument
     * is the integer id of the connection within this binding.
     */
    public osmo.metaedit.MENull objTargetOffset(osmo.metaedit.MEOop receiver, osmo.metaedit.MEAny objTargetOffset, int id) throws java.rmi.RemoteException;

    /**
     * Removes a breakpoint from a role representation in the receiver
     * argument, a diagram binding representation MEOop. The second argument
     * is an MEAny containing the breakpoint coordinate. The third argument
     * is the integer id of the connection within this binding.
     */
    public osmo.metaedit.MEAny removePoint(osmo.metaedit.MEOop receiver, osmo.metaedit.MEAny removePoint, int id) throws java.rmi.RemoteException;

    /**
     * Returns an MEOopArray of the axis elements on the horizontal
     * axis of the receiver argument, a matrix MEOop.
     */
    public osmo.metaedit.MEOop[] hAxis(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns an MEOopArray of the axis elements on the vertical
     * axis of the receiver argument, a matrix MEOop.
     */
    public osmo.metaedit.MEOop[] vAxis(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns the element to select from a binding for display in
     * a cell of the receiver argument, a matrix MEOop. The result returned
     * is a string corresponding to the choice in the View menu: firstRole
     * for the Row Role, secondRole for the Column Role, and relationship
     * for the Relationship.
     */
    public java.lang.String bindSelect(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Sets the element to select from a binding for display in a
     * cell of the receiver argument, a matrix MEOop. The value is a string
     * corresponding to the choice in the View menu: firstRole for the Row
     * Role, secondRole for the Column Role, and relationship for the Relationship.
     */
    public osmo.metaedit.MENull setBindSelect(osmo.metaedit.MEOop receiver, java.lang.String bindSelect) throws java.rmi.RemoteException;

    /**
     * Returns the cell display mode of the receiver argument, a matrix
     * MEOop. The result returned is a string corresponding to the choice
     * in the Cell menu: displayText, displaySymbol, or displayTextAndSymbol.
     */
    public java.lang.String cellDisplayMode(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Sets the cell display mode of the receiver argument, a matrix
     * MEOop. The value is a string corresponding to the choice in the Cell
     * menu: displayText, displaySymbol, or displayTextAndSymbol.
     */
    public osmo.metaedit.MENull setCellDisplayMode(osmo.metaedit.MEOop receiver, java.lang.String cellDisplayMode) throws java.rmi.RemoteException;

    /**
     * Returns the cell text display function of the receiver argument,
     * a matrix MEOop. The result returned is a string corresponding to the
     * choice in the Cell | Text Display dialog: userPrintStringWithType,
     * userPrintString, typeName, letter, justX.
     */
    public java.lang.String displayStringSelector(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Sets the cell text display function of the receiver argument,
     * a matrix MEOop. The value is a string corresponding to the choice
     * in the Cell | Text Display dialog: userPrintStringWithType, userPrintString,
     * typeName, letter, justX.
     */
    public osmo.metaedit.MENull setDisplayStringSelector(osmo.metaedit.MEOop receiver, java.lang.String displayStringSelector) throws java.rmi.RemoteException;

    /**
     * Returns the axis display mode of the receiver argument, a matrix
     * MEOop. The result returned is a string corresponding to the choice
     * in the Axis menu: axesDisplayText, axesDisplaySymbol, or axesDisplayTextAndSymbol.
     */
    public java.lang.String axisDisplayMode(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Sets the axis display mode of the receiver argument, a matrix
     * MEOop. The value is a string corresponding to the choice in the Axis
     * menu: axesDisplayText, axesDisplaySymbol, or axesDisplayTextAndSymbol.
     */
    public osmo.metaedit.MENull setAxisDisplayMode(osmo.metaedit.MEOop receiver, java.lang.String axisDisplayMode) throws java.rmi.RemoteException;

    /**
     * Returns whether the receiver argument, a matrix MEOop, only
     * shows bindings in cells corresponding to the first role, i.e. whether
     * the View | Directed? menu item is chosen.
     */
    public boolean showFirst(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Sets whether the receiver argument, a matrix MEOop, only shows
     * bindings in cells corresponding to the first role, i.e. whether the
     * View | Directed? menu item is chosen.
     */
    public osmo.metaedit.MENull setShowFirst(osmo.metaedit.MEOop receiver, boolean showFirst) throws java.rmi.RemoteException;

    /**
     * Returns the width in pixels of the row labels in the receiver
     * argument, a matrix MEOop.
     */
    public int rowLabelsWidth(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Sets the width in pixels of the row labels in the receiver
     * argument, a matrix MEOop.
     */
    public osmo.metaedit.MENull setRowLabelsWidth(osmo.metaedit.MEOop receiver, int rowLabelsWidth) throws java.rmi.RemoteException;

    /**
     * Returns the text style used by the receiver argument, a matrix
     * MEOop. The result returned is one of the styles shown in Format |
     * Font...
     */
    public java.lang.String textStyleSymbol(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Sets the text style used by the receiver argument, a matrix
     * MEOop. The value is one of the styles shown in Format | Font...
     */
    public osmo.metaedit.MENull setTextStyleSymbol(osmo.metaedit.MEOop receiver, java.lang.String textStyleSymbol) throws java.rmi.RemoteException;

    /**
     * Add to the receiver argument, a matrix MEOop, a new representation
     * for the Object MEOop. The representation is added to the horizontal
     * or vertical axis, depending on the toAxis argument, before the specified
     * index (1-based). Returns the MEOop of the resulting axis element.
     */
    public osmo.metaedit.MEOop addNewAxisEltFor(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop addNewRepFor, java.lang.String toAxis, int beforeIndex) throws java.rmi.RemoteException;

    /**
     * Returns the width in pixels of the receiver argument, a matrix
     * axis element MEOop.
     */
    public int getWidth(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Sets the width in pixels of the receiver argument, a matrix
     * axis element MEOop.
     */
    public osmo.metaedit.MENull setWidth(osmo.metaedit.MEOop receiver, int setWidth) throws java.rmi.RemoteException;

    /**
     * Returns the METype of the object type whose instances are shown
     * in the receiver argument, a table MEOop.
     */
    public osmo.metaedit.METype oT(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns an MEOopArray containing the object representations
     * in the receiver, a table MEOop.
     */
    public osmo.metaedit.MEOop[] baseCollection(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Returns the text style used by the receiver argument, a table
     * MEOop. The result returned is one of the styles shown in Format |
     * Font...
     */
    public java.lang.String font(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Sets the text style used by the receiver argument, a table
     * MEOop. The value is one of the styles shown in Format | Font...
     */
    public osmo.metaedit.MENull setFont(osmo.metaedit.MEOop receiver, java.lang.String font) throws java.rmi.RemoteException;

    /**
     * Returns an MEAnyArray containing the column widths in pixels
     * in the receiver, a table MEOop.
     */
    public osmo.metaedit.MEAny[] gxlWidths(osmo.metaedit.MEOop receiver) throws java.rmi.RemoteException;

    /**
     * Sets the column widths in pixels of the receiver, a table MEOop.
     * The value is an MEAnyArray containing either one MEAny integer, which
     * will become the default width for all columns, or as many MEAny integers
     * as there are properties in this table's object type.
     */
    public osmo.metaedit.MENull setGxlWidths(osmo.metaedit.MEOop receiver, osmo.metaedit.MEAny[] gxlWidths) throws java.rmi.RemoteException;

    /**
     * Adds a new object representation to the receiver argument,
     * a table MEOop. The second argument is the MEOop of the object to represent.
     * The third argument is a 1-based integer index of the row before which
     * to insert the new object representation. The return value is the MEOop
     * of the new object representation.
     */
    public osmo.metaedit.MENull addNewTableEltFor(osmo.metaedit.MEOop receiver, osmo.metaedit.MEOop addNewRepFor, int beforeIndex) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull currentDir(osmo.metaedit.MENull receiver, java.lang.String currentDir) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull fileInPatch(osmo.metaedit.MENull receiver, java.lang.String fileInPatch) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull fileInPatches(osmo.metaedit.MENull receiver) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull abandon(osmo.metaedit.MENull receiver) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull commit(osmo.metaedit.MENull receiver) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull condenseDatabase(osmo.metaedit.MENull receiver) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull createProject(osmo.metaedit.MENull receiver, java.lang.String createProject) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull document(osmo.metaedit.MENull receiver, java.lang.String document, java.lang.String into) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull forAll(osmo.metaedit.MENull receiver, java.lang.String forAll, java.lang.String run) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull forName(osmo.metaedit.MENull receiver, java.lang.String forName, java.lang.String type, java.lang.String run) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull login(osmo.metaedit.MENull receiver) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull loginDB(osmo.metaedit.MENull receiver, java.lang.String loginDB, java.lang.String user, java.lang.String password) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull loginNewDB(osmo.metaedit.MENull receiver, java.lang.String loginNewDB, java.lang.String dir, java.lang.String user, java.lang.String password) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull logoutAndExit(osmo.metaedit.MENull receiver) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull maintainDatabase(osmo.metaedit.MENull receiver) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull openAllProjects(osmo.metaedit.MENull receiver) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull quit(osmo.metaedit.MENull receiver) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull setProject(osmo.metaedit.MENull receiver, java.lang.String setProject) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull startAPI(osmo.metaedit.MENull receiver) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull startAPIHostname(osmo.metaedit.MENull receiver, java.lang.String startAPIHostname, java.lang.String port, java.lang.String logEvents) throws java.rmi.RemoteException;

    /**
     * See the documentation for the corresponding command-line argument
     */
    public osmo.metaedit.MENull stopAPI(osmo.metaedit.MENull receiver) throws java.rmi.RemoteException;
}
