package com.sun.corba.se.spi.activation;


/**
* com/sun/corba/se/spi/activation/EndpointInfoListHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from c:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8u201/12322/corba/src/share/classes/com/sun/corba/se/spi/activation/activation.idl
* Saturday, December 15, 2018 6:38:37 PM PST
*/

abstract public class EndpointInfoListHelper
{
  private static String  _id = "IDL:activation/EndpointInfoList:1.0";

  public static void insert (org.omg.CORBA.Any a, com.sun.corba.se.spi.activation.EndPointInfo[] that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static com.sun.corba.se.spi.activation.EndPointInfo[] extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = com.sun.corba.se.spi.activation.EndPointInfoHelper.type ();
      __typeCode = org.omg.CORBA.ORB.init ().create_sequence_tc (0, __typeCode);
      __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (com.sun.corba.se.spi.activation.EndpointInfoListHelper.id (), "EndpointInfoList", __typeCode);
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static com.sun.corba.se.spi.activation.EndPointInfo[] read (org.omg.CORBA.portable.InputStream istream)
  {
    com.sun.corba.se.spi.activation.EndPointInfo value[] = null;
    int _len0 = istream.read_long ();
    value = new com.sun.corba.se.spi.activation.EndPointInfo[_len0];
    for (int _o1 = 0;_o1 < value.length; ++_o1)
      value[_o1] = com.sun.corba.se.spi.activation.EndPointInfoHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, com.sun.corba.se.spi.activation.EndPointInfo[] value)
  {
    ostream.write_long (value.length);
    for (int _i0 = 0;_i0 < value.length; ++_i0)
      com.sun.corba.se.spi.activation.EndPointInfoHelper.write (ostream, value[_i0]);
  }

}
