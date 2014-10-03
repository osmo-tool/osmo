package osmo.mjexamples.gsm;

public class File
{
  public SimCard.File_Type type;
  public SimCard.F_Name name;
  public String data;
  public SimCard.Permission perm_read;
  public File parent;   // null means this is the root file (MF).
  
  public File(
      SimCard.File_Type type0,
      SimCard.F_Name name0,
      String data0,
      SimCard.Permission perm0,
      File parent0)
  {
    type = type0;
    name = name0;
    data = data0;
    perm_read = perm0;
    parent = parent0;
  }
}
