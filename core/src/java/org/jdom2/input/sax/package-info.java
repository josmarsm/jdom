/**
Tools to build JDOM documents and content using SAX parsers.

<h2>Introduction</h2>
Skip to the <a href="Examples">Examples</a> section for a quick bootstrap. 
<p>
The {@link org.jdom2.input.SAXBuilder} class parses input and produces JDOM
output. It does this using three 'pillars' of functionality, which when combined
constitute a 'parse'.
<p>
The three pillars are:
<ol>
<li>The SAX Parser - this is a 'third-party' parser such as Xerces.
<li>The SAX Event Handler - which reads the data produced by the parser
<li>The JDOMFactory - which converts the resulting data in to JDOM content
</ol>
There are many different ways of parsing the document from its input state
(DocType-validating, etc.), and there are also different ways to interpret
the SAX events. Finally there are different ways to produce JDOM Content using
different implementations of the JDOMFactory.
<p>
SAXBuilder provides a central location where these three pillars are configured.
Some configuration settings require coordinated changes to both the SAX parser
and the SAX handler, and SAXBuilder ensures the coordination is maintained.

<h2>Setting the Pillars</h2>
SAXBuilder provides mechanisms for stipulating what the three pillars will be
using a number of different mechanisms:
<ul>
<li>A Constructor: {@link org.jdom2.input.SAXBuilder#SAXBuilder(XMLReaderJDOMFactory, SAXHandlerFactory, org.jdom2.JDOMFactory)}
<li>A default constructor {@link org.jdom2.input.SAXBuilder#SAXBuilder()}
    that chooses a non-validating JAXP sourced XMLReader Factory
    {@link org.jdom2.input.sax.XMLReaderJAXPSingletons#NONVALIDATING} which it
    mates with a Default {@link org.jdom2.input.sax.SAXHandler} factory, and the 
    {@link org.jdom2.DefaultJDOMFactory}. 
<li>A number of other constructors that mostly are for backward-compatibility
    with JDOM 1.x. These other constructors affect what 
    {@link org.jdom2.input.sax.XMLReaderJDOMFactory} will be used but still use
    the default SAXHandler and JDOMFactory values.
<li>Methods to change whatever was constructed:
	<ul>
	<li>{@link org.jdom2.input.SAXBuilder#setXMLReaderFactory(XMLReaderJDOMFactory)}
	<li>{@link org.jdom2.input.SAXBuilder#setSAXHandlerFactory(SAXHandlerFactory)}
	<li>{@link org.jdom2.input.SAXBuilder#setJDOMFactory(org.jdom2.JDOMFactory)}
	</ul>
</ul>


<h2>The XMLReaderJDOMFactory Pillar</h2>

SAX parsers have been exposed as different things during the evolution of the
SAX API, but in SAX2.0 they are <code>XMLReader</code> instances. Thus
SAXBuilder needs an XMLReader to process the input. To get an XMLReader the
SAXBuilder delegates to the {@link org.jdom2.input.sax.XMLReaderJDOMFactory}
by calling {@link org.jdom2.input.sax.XMLReaderJDOMFactory#createXMLReader()} 
<p>
XMLReader instances can be created in a few different ways, and also they
can be set to perform the SAX parse in a number of different ways. The classes
in this package are designed to make it easier and faster to locate the XMLReader
that is suitable for the XML parsing you intend to do. At the same time, if the
parsing you intend to do is outside the normal bounds of how JDOM is used, you
still have the functionality to create a completely custom mechanism for setting
the XMLReader for SAXBuilder.
<p>
There are two typical ways to specify and create an XMLReader
instance: using JAXP and using the SAX2.0 API. If necessary you can also create
direct instances of XMLReader implementations using 'new' constructors, but
each SAX implementation has different class names for their SAX drivers so doing
raw constructors is not portable and not recommended.
<p>
Where possible it is recommended that you use the JAXP mechanism for obtaining
XMLReaders because:
<ul>
<li>It is more 'modern'.
<li>It provides a more consistent interface to different SAX implementations 
<li>It provides cleaner and more portable support for validating using the
		{@link javax.xml.validation.Validator} mechanisms.
<li>It allows you to create differently-configured 'factories' that
    create XMLReaders in a pre-specified format (SAX2.0 has a single global
    factory that creates raw XMLReader instances that then need to be
    re-configured for you task). 
</ul>

<h3>JAXP Factories</h3>
JDOM exposes four factories that use JAXP to source XMLReaders. These four
factories cover almost all conditions under which you would want a SAX parser:
<ol>
<li>A simple non-validating SAX parser
<li>A validating parser that uses the DTD references in the XML to validate
    against.
<li>A validating parser that uses the XML Schema (XSD) references embedded in
    the XML to validate against.
<li>A validating parser that uses an external Schema (XML Schema, RelaxNG, etc)
    to validate the XML against.
</ol>
The first three are all relatively simple, and are available as members of the
{@link org.jdom2.input.sax.XMLReaderJAXPSingletons} enumeration. These members
are 'singletons' that can be used in a multi-threaded and concurrent way to
provide XMLReaders that are configured correctly for the respective behaviour.
<p>
To validate using an external Schema you can use the
{@link org.jdom2.input.sax.XMLReaderJAXPSchemaFactory} to create an instance for
the particular Schema you want to validate against. Because this requires an
input Schema it cannot be constructed as a singleton like the others.

<h3>SAX 2.0 Factory</h3>

JDOM does support using the SAX 2.0 API for creating XMLReaders though using
either the 'default' SAX 2.0 implementation or a particular SAX Driver class
name by creating instances of the
{@link org.jdom2.input.sax.XMLReaderSAX2Factory}.
<p>
JDOM does not provide a preconfigured way to do XML Schema validation though.
SAX 2.0 API does not expose a convenient way to configure different SAX 
implementations in a consistent way, so it is up to the JDOM user to wrap the
XMLReaderSAX2Factory in such a way that it reconfigures the XMLReader to be
appropriate for the task at hand.

<h3>Custom Factories</h3>
If your circumstances require it you can create your own implementation of the
{@link org.jdom2.input.sax.XMLReaderJDOMFactory} to provide XMLReaders configured
as you like them. It will probably be best if you wrap an existing implementation
with your custom code though in order to get the best results fastest.
<p>
Note that the existing JDOM implementations described above all set the
generated XMLReaders to be namespace-aware and to supply namespace-prefixes.
Custom implementations should also ensure that this is set unless you absolutely
know what you are doing.


<h2>The SAXHandlerFactory Pillar</h2>

The SAXHandler interprets the SAX calls and provides the information to the
JDOMFactory to create JDOM content. SAXBuilder creates a SAXHandler from the
{@link org.jdom2.input.sax.SAXHandlerFactory} pillar. It is unusual for a JDOM
user to need to customise the manner in which this happens, but, in the event
that you do you can create a subclass of the SAXHandler class, and then create
an instance of the SAXHandlerFactory that returns new subclass instances.
This new factory can become a pillar in SAXBuilder and supply custom SAXHandlers
to the parse process.  
 
 
<h2>The JDOMFactory Pillar</h2>

There are a couple of reasons for changing the JDOMFactory pillar in SAXBuilder.
The default JDOMFactory used is the {@link org.jdom2.DefaultJDOMFactory}. This
factory validates the values being used to create JDOM content. There is also
the {@link org.jdom2.UncheckedJDOMFactory} which does not validate the data, so
it should only be used if you are absolutely certain that your SAX source can
never provide illegal content. You may have other reasons for creating a custom
JDOMFactory such as if you need to create custom versions of JDOM Content like
a custom Element subclass. 

<h2>Configuring the Pillars</h2>

The JDOMFactory pillar is not configurable, you can only replace it entirely.
The other two pillars are configurable though, but you should inspect the
getters and setters on {@link org.jdom2.input.SAXBuilder} to identify what can
(by default) be changed easily. Remember, if you have anything that needs to be
customised beyond what SAXBuilder offers you can always replace a pillar with a
custom implementation.

<h2>Execution Model</h2>
Once all the pillars are set and configured to your satisfaction you can 'build'
a JDOM Document from a source. The actual parse process consists of a 'setup',
'parse', and 'reset' phase.
<p>
The setup process involves obtaining an XMLReader from the XMLReaderJDOMFactory
and a SAXHandler (configured to use the JDOMFactory) from the SAXHandlerFactory.
These two instances are then configured to meet the settings specified on
SAXBuilder, and once configured they are 'compiled' in to a SAXBuilderEngine.
<p>
The SAXBuilderEngine is a non-configurable 'embodiment' of the configuration of
the SAXBuilder when the engine was created, and it contains the entire
'workflow' necessary to parse the input in to JDOM content. Further, it is a
guarantee that the XMLReader and SAXHandler instances in the SAXBuilderEngine
are never shared with any other engine or entity (assuming that the respective
factories never issue the same instances multiple times).
<p>
The 'parse' phase starts once the setup phase is complete and the
SAXBuilderEngine has been created. The created engine is used to parse the input,
and the resulting Document is returned to the client.
<p>
The 'reset' phase happens after the completion of the 'parse' phase, and it
resets the SAXBuilderEngine to its initial state, ready to process the next
parse request.

<h2>Parser Reuse</h2>
A large amount of the effort involved in parsing the document is actually the
creation of the XMLReader and the SAXHandler instances, as well as applying the
configuration to those instances (the 'setup' phase).
<p>
JDOM2 uses the new SAXBuilderEngine to represent the state of the SAXBuilder
at the moment prior to the parse. SAXBuilder will then 'remember' and reuse this
exact SAXBuilderEngine until something changes in the SAXBuilder configuration.
As soon as the configuration changes in any way the engine will be forgotten and
a new one will be created when the SAXBuilder next parses a document.
<p>
If you turn off parser reuse with
{@link org.jdom2.input.SAXBuilder#setReuseParser(boolean)} then SAXBuilder will
immediately forget the engine, and it will also forget it after each build (i.e.
SAXBuilder will create a new SAXBuilderEngine each parse).
<p>
It follows then that as long as you do not change the SAXBuilder configuration
then the SAXBuilder will always reuse the same SAXBuilderEngine. This is very
efficient because there is no configuration management between parses, and the
procedure completely eliminates the 'setup' component for all but the first
parse. 

<h2>Parser Pooling</h2>
In order to facilitate Parser pooling it is useful to export the
SAXBuilderEngine as a stand-alone reusable parser. At any time you can call
{@link org.jdom2.input.SAXBuilder#buildEngine()} and you can get a newly
created SAXBuilderEngine instance. The SAXBuilderEngine has the same 'build'
methods as SAXBuilder, and these are exposed as the
{@link org.jdom2.input.sax.SAXEngine} interface. Both SAXBuilder and 
SAXBuilderEngine implement the SAXEngine interface. Thus, if you use Parser
pools you can pool either the SAXBuilder or the SAXBuilderEngine in the same
pool.
<p>
it is most likely though that what you will want to do is to create a single
SAXBuilder that represents the configuration you want, and then you can use this
single SAXBuilder to create multiple SAXEngines as you need them in the pool by
calling the <code>buildEngine()</code> method.

<h2 name="Examples">Examples</h2>
Create a simple SAXBuilder and parse a document:
<p>
<pre>
    SAXBuilder sb = new SAXBuilder();
    Document doc = sb.build(new File("file.xml"));
</pre>
<p>
Create a DTD validating SAXBuilder and parse a document:
<p>
<pre>
    SAXBuilder sb = new SAXBuilder(XMLReaderJAXPSingletons.DTDVALIDATING);
    Document doc = sb.build(new File("file.xml"));
</pre>
Create an XSD (XML Schema) validating SAXBuilder and parse a document:
<p>
<pre>
    SAXBuilder sb = new SAXBuilder(XMLReaderJAXPSingletons.XSDVALIDATING);
    Document doc = sb.build(new File("file.xml"));
</pre>
<p>

*/
package org.jdom2.input.sax;