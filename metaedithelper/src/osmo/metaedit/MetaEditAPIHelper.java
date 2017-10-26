package osmo.metaedit;
import java.rmi.RemoteException;
import java.util.*;

import javax.xml.rpc.ServiceException;

/**
 * This class helps MetaEdit+ api interface usage
 * @author Olli-Pekka@Puolitaival.fi
 */
public class MetaEditAPIHelper {

  MetaEditAPI service =  new MetaEditAPILocator();
  MetaEditAPIPortType port = null;
  private boolean debug = true;
  
  /**
   * Constructor makes initializations
   */
  public MetaEditAPIHelper(){
  }
  
  public void setDebug(boolean d){
    debug = d;
  }
  
  /**
   * Refresh a graph
   * @param graphID is a graph oid i.e. 1_1234
   */
  public void refresh(String graphID){

    MEOop graph = createMEOop(graphID);
    
    try {
      port = service.getMetaEditAPIPort();
    } catch (ServiceException e) {
      System.out.println("ERROR ");
      return;
    }
    try {
      port.refresh(graph);
    } catch (RemoteException e) {
      printError("Cannot connect to the MetaEdit API");
    }
  }
  
  /**
   * Animates given object in given graph
   * @param graphID is a graph oid i.e. 1_1234
   * @param objectID is a object oid i.e. 1_1234
   */
  public void animate(String graphID, String objectID){
    MetaEditAPI service =  new MetaEditAPILocator();
    MetaEditAPIPortType port = null;
    
    MEOop graph = createMEOop(graphID);
    MEOop object = createMEOop(objectID);
    
      try {
        port = service.getMetaEditAPIPort();
      } catch (ServiceException e) {
        System.out.println("ERROR ");
        return;
      }
      try {
        port.animate(graph, object);
      } catch (RemoteException e) {
        printError("Cannot connect to the MetaEdit API");
      }
  }
  
  /**
   * 
   * @param resOid
   * @param propIndex
   * @param val
   */
  public void setStringAt(String resOid, int propIndex, String string){
    
    //System.out.println("string:"+string);
    MEOop receiver = createMEOop(resOid);
    MEAny value = createMEAny("String", "'"+string+"'");
    
    try {
      port = service.getMetaEditAPIPort();
    } catch (ServiceException e) {
      System.out.println("ERROR in setStringAt()");
      return;
    }
    try {
      MEAny temp = port.setValueAt(receiver, propIndex, value);
    } catch (RemoteException e) {
      printError("ERROR"+e.toString());
    }
  }
  
  
  
  /**
   * A common exception handler
   * @param e
   */
  private void printError(String description){
    if(debug)
      System.out.println("ERROR: "+description);
  }
  
  /**
   * Created MEOop
   * @param oid 
   * @return an MEOop object
   */
  private MEOop createMEOop(String oid){
    MEOop meo = new MEOop();
    StringTokenizer st = new StringTokenizer(oid, "_", false);
    meo.setAreaID(Integer.parseInt(st.nextToken()));
    meo.setObjectID(Integer.parseInt(st.nextToken()));
    return meo;
  }
  
  /**
   * Function helps for creating MEAny objects
   * @param type is a type like MEOop, MENull, String, Text, Integer, Point or OrderedCollection
   * @param value is the actual value, stored as a string representation:
   * @return
   */
  private MEAny createMEAny(String type, String value){
    MEAny tempAny = new MEAny();
    //MEOop, MENull, String, Text, Integer, Point or OrderedCollection
    tempAny.setMeType(type);
    tempAny.setMeValue(value);
    return tempAny;
  }
  
  
  public void setText() throws Exception{
    MetaEditAPI service =  new MetaEditAPILocator();
    MetaEditAPIPortType port = service.getMetaEditAPIPort();
    METype graphType =  new METype();
    MEOop[] graphs;
    METype objectType = new METype();
    MEAny[] props = new MEAny[1];
    MEAny[] values = new MEAny[1];
    MEAny area = new MEAny();
    MEAny np = new MEAny();
    MEAny objectInfo = new MEAny();
    MEOop newObject = new MEOop();
    MEOop animObject = new MEOop();
    MEAny tempAny;

    graphType.setName("Test model");
    graphs = port.allGoodInstances(graphType);
    objectType.setName("Note");
    
    //MENull animate (MEOop receiver, MEOop animate)
    //animObject.setAreaID(areaID)
    //objectInfo = port.animate(objectType, animObject)
    
    //MEAny instProps (METype receiver, MEAnyArray propColl, MEAnyArray valueColl, MEAny np, MEAny inArea)
    tempAny = new MEAny();
    tempAny.setMeType("3_1239");//MENull
    tempAny.setMeValue("");
    props[0] = tempAny;
    tempAny = new MEAny();
    tempAny.setMeType("Text");
    tempAny.setMeValue("'Jee toimii!!'");
    values[0] = tempAny;
    np.setMeType("MENull"); //note
    np.setMeValue("");
    area.setMeType("3_249"); //graph
    area.setMeValue("");
    objectInfo = port.instProps(objectType, props, values, np, area);
    StringTokenizer st = new StringTokenizer(objectInfo.getMeValue(), "_", false);
    newObject.setAreaID(Integer.parseInt(st.nextToken()));
    newObject.setObjectID(Integer.parseInt(st.nextToken()));
    port.addToGraph(newObject, graphs[0]);
  }
  }