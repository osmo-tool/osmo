<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.metacase.com/gxlGOPRRType" xmlns:sym="http://www.metacase.com/symbol">
	<graph description="" type="Graph_OSMO_model_user_3496916370" typeName="OSMO model">
		<slot id="a0y3we" name="Name" unique="false">
			<property description="" id="Property_Name_user_3496916383" type="Property_Name_user_3496916383" typeName="Name">
				<dataType>
					<simpleType>String</simpleType>
				</dataType>
				<widget>Input Field</widget>
			</property>
		</slot>
		<slot id="a2ypye" name="Generate in folder" unique="false">
			<property description="" type="Property_FilePath_user_3496930675" typeName="FolderPath">
				<dataType>
					<simpleType>String</simpleType>
				</dataType>
				<widget>Input Field</widget>
			</property>
		</slot>
		<slot id="a1yppe" name="Debug" unique="false">
			<property description="" type="Property_Debug_user_3496930513" typeName="Debug">
				<dataType>
					<simpleType>Boolean</simpleType>
				</dataType>
			</property>
		</slot>
		<identProp slotID="a0y3we"></identProp>
		<object description="" id="Object_EndState_user_3496916452" type="Object_EndState_user_3496916452" typeName="EndState">
			<objectSymbol offset="50,40" scaleFilter="1,1" xmlns="http://www.metacase.com/symbol">
				<defaultConnectable isSticky="true" points="50,40 70,40 70,60 50,60 50,40" targetPointX="60" targetPointY="50" usesGrid="false"></defaultConnectable>
				<svg baseProfile="tiny" height="60" version="1.2" width="70" xmlns="http://www.w3.org/2000/svg">
					<ellipse cx="60" cy="50" fill="rgb(255,255,255)" rx="10" ry="10" stroke="rgb(0,0,0)" stroke-width="1" sym:startAngle="3.6e2" sym:sweepAngle="-3.6e2">
						<metaInfo xmlns="http://www.metacase.com/symbol"></metaInfo>
					</ellipse>
					<ellipse cx="60" cy="50" fill="rgb(0,0,0)" rx="7" ry="7" stroke="rgb(0,0,0)" stroke-width="1" sym:startAngle="3.6e2" sym:sweepAngle="-3.6e2">
						<metaInfo xmlns="http://www.metacase.com/symbol"></metaInfo>
					</ellipse>
				</svg>
			</objectSymbol>
		</object>
		<object description="" id="Object_StartState_user_3496916418" type="Object_StartState_user_3496916418" typeName="StartState">
			<objectSymbol offset="80,40" scaleFilter="1,1" xmlns="http://www.metacase.com/symbol">
				<defaultConnectable isSticky="true" points="80,40 100,40 100,60 80,60 80,40" targetPointX="90" targetPointY="50" usesGrid="false"></defaultConnectable>
				<svg baseProfile="tiny" height="60" version="1.2" width="100" xmlns="http://www.w3.org/2000/svg">
					<ellipse cx="90" cy="50" fill="rgb(0,0,0)" rx="10" ry="10" stroke="rgb(0,0,0)" stroke-width="1" sym:startAngle="3.6e2" sym:sweepAngle="-3.6e2">
						<metaInfo xmlns="http://www.metacase.com/symbol"></metaInfo>
					</ellipse>
				</svg>
			</objectSymbol>
			<icon offset="50,20" xmlns="http://www.metacase.com/icon">
				<svg baseProfile="tiny" height="40" version="1.2" width="70" xmlns="http://www.w3.org/2000/svg">
					<ellipse cx="60" cy="30" fill="rgb(0,0,0)" rx="10" ry="10" stroke="rgb(0,0,0)" stroke-width="1" sym:startAngle="3.6e2" sym:sweepAngle="-3.6e2"></ellipse>
				</svg>
			</icon>
		</object>
		<object description="" id="Object_State_user_3496916528" type="Object_State_user_3496916528" typeName="State">
			<slot id="a0y44c" name="Name" unique="true">
				<property href="#Property_Name_user_3496916383"></property>
			</slot>
			<slot id="a1atn0" name="weight" unique="false">
				<property description="" type="Property_weight_user_3497212998" typeName="weight">
					<dataType>
						<simpleType>Number</simpleType>
					</dataType>
					<defaultValue>
						<int>10</int>
					</defaultValue>
					<regex>[0-9]*</regex>
				</property>
			</slot>
			<objectSymbol offset="30,40" scaleFilter="1,1" xmlns="http://www.metacase.com/symbol">
				<defaultConnectable isSticky="true" points="30,40 160,40 160,110 30,110 30,40" targetPointX="95" targetPointY="75" usesGrid="false"></defaultConnectable>
				<svg baseProfile="tiny" height="110" version="1.2" width="160" xmlns="http://www.w3.org/2000/svg">
					<defs>
						<linearGradient gradientUnits="objectBoundingBox" id="gradient1" sym:correctGamma="true" x1="0.31" x2="0.86" y1="0.185714" y2="2.12857">
							<stop offset="0" stop-color="rgb(255,255,255)"></stop>
							<stop offset="1" stop-color="rgb(0,255,0)"></stop>
						</linearGradient>
					</defs>
					<rect fill="url(#gradient1)" height="70" rx="8" ry="8" stroke="rgb(0,0,0)" stroke-width="1" width="130" x="30" y="40">
						<metaInfo xmlns="http://www.metacase.com/symbol"></metaInfo>
					</rect>
					<textArea display-align="center" fill="rgb(0,0,0)" font-family="#sans serif" font-size="14" font-style="normal" font-weight="bold" height="20" sym:textboxFill="none" sym:textboxStroke="none" sym:textboxStroke-width="1" sym:wordWrap="true" text-anchor="middle" width="130" x="30" y="40">
						PropertyTextSource:a0y44c
						<metaInfo xmlns="http://www.metacase.com/symbol"></metaInfo>
					</textArea>
					<textArea display-align="before" fill="rgb(128,128,128)" font-family="#sans serif" font-size="14" font-style="normal" font-weight="normal" height="20" sym:textboxFill="none" sym:textboxStroke="none" sym:textboxStroke-width="1" sym:wordWrap="true" text-anchor="middle" width="130" x="30" y="60">
						PropertyTextSource:a1atn0
						<metaInfo xmlns="http://www.metacase.com/symbol"></metaInfo>
					</textArea>
				</svg>
			</objectSymbol>
		</object>
		<object description="" type="Object_GenerationSetup_user_3496952374" typeName="GenerationSetup">
			<slot id="a0zng4" name="Name" unique="false">
				<property href="#Property_Name_user_3496916383"></property>
			</slot>
			<slot id="a1zo9t" name="Algorithm" unique="false">
				<property description="" type="Property_Algorithm_user_3496952912" typeName="Algorithm">
					<dataType>
						<simpleType>String</simpleType>
					</dataType>
					<defaultValue>
						<string>RandomAlgorithm</string>
					</defaultValue>
					<widget>Fixed List</widget>
					<listValues>
						<string>RandomAlgorithm</string>
						<string>LessRandomAlgorithm</string>
						<string>WeightedRandomAlgorithm</string>
						<string>ManualDrive</string>
					</listValues>
				</property>
			</slot>
			<slot id="a2zruy" name="TestCaseEndCondition" unique="false">
				<property description="" id="Property_TestCaseEndCondition_user_3496955237" type="Property_TestCaseEndCondition_user_3496955237" typeName="EndCondition">
					<dataType>
						<object description="" type="Object_EndCondition_user_3496954868" typeName="Endless">
							<identReport>type</identReport>
						</object>
					</dataType>
				</property>
			</slot>
			<slot id="a3zsnr" name="TestSuiteEndCondition" unique="false">
				<property href="#Property_TestCaseEndCondition_user_3496955237"></property>
			</slot>
			<identProp slotID="a0zng4"></identProp>
			<objectSymbol offset="30,20" scaleFilter="1,1" xmlns="http://www.metacase.com/symbol">
				<defaultConnectable isSticky="true" points="30,20 140,20 140,90 30,90 30,20" targetPointX="85" targetPointY="55" usesGrid="false"></defaultConnectable>
				<svg baseProfile="tiny" height="90" version="1.2" width="140" xmlns="http://www.w3.org/2000/svg">
					<defs>
						<linearGradient gradientUnits="objectBoundingBox" id="gradient1" sym:correctGamma="true" x1="0.355556" x2="0.811111" y1="0.0499999" y2="2.8">
							<stop offset="0" stop-color="rgb(255,255,255)"></stop>
							<stop offset="1" stop-color="rgb(0,0,255)"></stop>
						</linearGradient>
					</defs>
					<rect fill="url(#gradient1)" height="70" rx="6" ry="6" stroke="rgb(0,0,0)" stroke-width="1" width="110" x="30" y="20">
						<metaInfo xmlns="http://www.metacase.com/symbol"></metaInfo>
					</rect>
					<textArea display-align="center" fill="rgb(0,0,0)" font-family="#sans serif" font-size="16" font-style="normal" font-weight="bold" height="20" sym:textboxFill="none" sym:textboxStroke="none" sym:textboxStroke-width="1" sym:wordWrap="true" text-anchor="middle" width="110" x="30" y="20">
						PropertyTextSource:a0zng4
						<metaInfo xmlns="http://www.metacase.com/symbol"></metaInfo>
					</textArea>
					<textArea display-align="center" fill="rgb(0,0,0)" font-family="#sans serif" font-size="12" font-style="normal" font-weight="normal" height="10" sym:textboxFill="none" sym:textboxStroke="none" sym:textboxStroke-width="1" sym:wordWrap="true" text-anchor="start" width="100" x="40" y="40">
						PropertyTextSource:a1zo9t
						<metaInfo xmlns="http://www.metacase.com/symbol"></metaInfo>
					</textArea>
					<textArea display-align="center" fill="rgb(0,0,0)" font-family="#sans serif" font-size="12" font-style="normal" font-weight="normal" height="10" sym:textboxFill="none" sym:textboxStroke="none" sym:textboxStroke-width="1" sym:wordWrap="true" text-anchor="start" width="100" x="40" y="50">
						ReportTextSource:'Case: ':TestCaseEndCondition
						<metaInfo xmlns="http://www.metacase.com/symbol"></metaInfo>
					</textArea>
					<textArea display-align="center" fill="rgb(0,0,0)" font-family="#sans serif" font-size="12" font-style="normal" font-weight="normal" height="10" sym:textboxFill="none" sym:textboxStroke="none" sym:textboxStroke-width="1" sym:wordWrap="true" text-anchor="start" width="100" x="40" y="60">
						ReportTextSource:'Suite: ':TestSuiteEndCondition
						<metaInfo xmlns="http://www.metacase.com/symbol"></metaInfo>
					</textArea>
				</svg>
			</objectSymbol>
		</object>
		<relationship description="" id="Relationship_Connector_user_3496916794" type="Relationship_Connector_user_3496916794" typeName="Transition">
			<slot id="a0yohp" name="Name" unique="false">
				<property href="#Property_Name_user_3496916383"></property>
			</slot>
			<identProp slotID="a0yohp"></identProp>
			<relationshipSymbol offset="50,40" scaleFilter="1,1" xmlns="http://www.metacase.com/symbol">
				<defaultConnectable isSticky="true" targetPointX="100" targetPointY="60" usesGrid="false"></defaultConnectable>
				<svg baseProfile="tiny" height="60" version="1.2" width="150" xmlns="http://www.w3.org/2000/svg">
					<textArea display-align="center" fill="rgb(0,0,0)" font-family="#sans serif" font-size="14" font-style="normal" font-weight="normal" height="20" sym:textboxFill="none" sym:textboxStroke="none" sym:textboxStroke-width="1" sym:wordWrap="true" text-anchor="middle" width="100" x="50" y="40">
						PropertyTextSource:a0yohp
						<metaInfo xmlns="http://www.metacase.com/symbol"></metaInfo>
					</textArea>
				</svg>
			</relationshipSymbol>
			<icon offset="61,22" xmlns="http://www.metacase.com/icon">
				<svg baseProfile="tiny" height="40" version="1.2" width="104" xmlns="http://www.w3.org/2000/svg">
					<polygon fill="rgb(0,0,0)" points="90,30 90,40 100,35" stroke="rgb(0,0,0)" stroke-width="1"></polygon>
					<line stroke="rgb(0,0,0)" stroke-width="1" x1="94" x2="72" y1="35" y2="35"></line>
					<textArea display-align="before" fill="rgb(0,0,0)" font-family="#sans serif" font-size="11" font-style="normal" font-weight="normal" height="14" sym:textboxFill="none" sym:textboxStroke="none" sym:textboxStroke-width="1" sym:wordWrap="true" text-anchor="start" width="43" x="61" y="22">Name</textArea>
				</svg>
			</icon>
		</relationship>
		<relationship description="" id="Relationship_FromStartTransition_user_3497016285" type="Relationship_FromStartTransition_user_3497016285" typeName="NamelessTransition">
			<icon offset="50,40" xmlns="http://www.metacase.com/icon">
				<svg baseProfile="tiny" height="50" version="1.2" width="80" xmlns="http://www.w3.org/2000/svg">
					<line stroke="rgb(0,0,0)" stroke-width="1" x1="50" x2="70" y1="45" y2="45"></line>
					<polygon fill="rgb(0,0,0)" points="70,40 70,50 80,45" stroke="rgb(0,0,0)" stroke-width="1"></polygon>
				</svg>
			</icon>
		</relationship>
		<role description="" id="Role_From_user_3496916730" type="Role_From_user_3496916730" typeName="From"></role>
		<role description="" id="Role_To_user_3496916736" type="Role_To_user_3496916736" typeName="To">
			<roleSymbol xmlns="http://www.metacase.com/symbol">
				<rolelineGOs stroke="rgb(0,0,0)" stroke-width="1">
					<metaInfo shouldRotate="false"></metaInfo>
				</rolelineGOs>
				<svg baseProfile="tiny" height="90" version="1.2" width="150" xmlns="http://www.w3.org/2000/svg">
					<defs>
						<linearGradient gradientUnits="objectBoundingBox" id="gradient1" sym:correctGamma="true" x1="0.45" x2="0.3" y1="-0.1" y2="1.35">
							<stop offset="0" stop-color="rgb(255,255,255)"></stop>
							<stop offset="1" stop-color="rgb(0,0,0)"></stop>
						</linearGradient>
					</defs>
					<polygon fill="url(#gradient1)" points="150,80 130,70 130,90" stroke="rgb(0,0,0)" stroke-width="1">
						<metaInfo shouldRotate="true" xmlns="http://www.metacase.com/symbol"></metaInfo>
					</polygon>
				</svg>
			</roleSymbol>
		</role>
		<binding>
			<relationship href="#Relationship_FromStartTransition_user_3497016285"></relationship>
			<connection>
				<cardinality start="1" stop="1"></cardinality>
				<role href="#Role_From_user_3496916730"></role>
				<object href="#Object_StartState_user_3496916418"></object>
			</connection>
			<connection>
				<cardinality start="1" stop="1"></cardinality>
				<role href="#Role_To_user_3496916736"></role>
				<object href="#Object_State_user_3496916528"></object>
			</connection>
		</binding>
		<binding>
			<relationship href="#Relationship_Connector_user_3496916794"></relationship>
			<connection>
				<cardinality start="1" stop="1"></cardinality>
				<role href="#Role_From_user_3496916730"></role>
				<object href="#Object_State_user_3496916528"></object>
			</connection>
			<connection>
				<cardinality start="1" stop="1"></cardinality>
				<role href="#Role_To_user_3496916736"></role>
				<object href="#Object_EndState_user_3496916452"></object>
				<object href="#Object_State_user_3496916528"></object>
			</connection>
		</binding>
		<report>Report '!OSMO'

/*

This generates modes for OSMO tester
Osmo source code and web site: http://osmo.googlecode.com/
@Author: olli-pekka@puolitaival.fi

*/

subreport '_translators' run

/*Generate state machine*/
if :Generate in folder; &lt;&gt; '' then
	filename :Generate in folder; :Name%var '.java' write
		subreport '_pre' run 
		subreport '_statemachine' run
		subreport '_post' run
	close 
else
	subreport '_pre' run 
	subreport '_statemachine' run
	subreport '_post' run
endif



/*Generate test generation setups*/
foreach .GenerationSetup;
{	
	if :Generate in folder;-1; &lt;&gt; '' then
		filename :Generate in folder;-1; :Name;-1;'_':Name%var '.java' write
			subreport '_testGenerationSetup' run
		close
	else
		subreport '_testGenerationSetup' run
	endif
}


endreport</report>
		<report>Report '!Test'

foreach .StartState
{
	subreport '_findEndState' run
}
endreport</report>
		<report>Report 'fromState'

do ~From.()
{
	  subreport '_stateName' run
}

endreport</report>
		<report>Report '_classStart'

'
/*
* This is automatically generated OSMO model
* Generated model author: Olli-Pekka Puolitaival
*/
public class ':Name%var' {
'

endreport</report>
		<report>Report '_endCondition'

if type = 'Endless'               then type '('                   ')' endif
if type = 'Length'                then type '(' :Length;          ')' endif
if type = 'RequirementsCoverage'  then type '(' :Coverage \(\%\); ')' endif
if type = 'TransitionCoverage'    then type '(' :Coverage \(\%\); ')' endif

endreport</report>
		<report>Report '_endstates'
'
	@EndCondition
	public boolean endCondition(){
		return '
	foreach .EndState
	{
		do ~To&gt;Transition~From.(){
			'currentState == State.'subreport '_stateName' run ' || '
		}
	}
	'false;
	}' newline
endreport</report>
		<report>Report '_findEndState'

/*
If it finds at least one end state this prints T
*/

if $endStateFound &lt;&gt; 'T' then

if type = 'EndState' then
	$endStateFound = 'T'
	'T'
else
	do ~From&gt;()~To.()
	{
			subreport '_findEndState' run	
	}
endif

endif
endreport</report>
		<report>Report '_guard'

	newline
	/*Annoitation*/
	'	@Guard("'subreport '_transitionName' run'")'newline
	
	/*Function header*/
	'	public boolean Guard_'subreport '_transitionName' run'(){' newline
	
	/*return value*/
	'		return '
	do ~From.()
	{
		'currentState == State.'subreport '_stateName' run
	}
	';' newline
	'	}'newline
	newline

endreport</report>
		<report>Report '_imports'

'
import osmo.tester.OSMOTester;
import osmo.tester.annotation.*;
import osmo.tester.annotation.EndCondition;
import osmo.tester.generator.endcondition.*;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.generator.algorithm.*;
'

endreport</report>
		<report>Report '_name'

if :Name = '' then
	oid
else
	:Name%var
endif

endreport</report>
		<report>Report '_post'

'
	/**
	* Basic generation algorithm with default settings
	*/
	public static void main(String[] args) {		
		OSMOTester tester = new OSMOTester();' newline
		
		/*If there is end state it means that test case can be ended only in that state*/
		foreach .EndState;{
			$EndState = 'T'
		}
		if $EndState = 'T' then
			'		tester.addTestEndCondition(new Endless());' newline
		endif
		
'		tester.addModelObject(new ':Name'());' newline
'		tester.generate();
	}
}' newline
endreport</report>
		<report>Report '_pre'

subreport '_imports' run
subreport '_classStart' run

/*States in enumerate*/
'	private enum State{' newline	
	subreport '_StateEnumList' run
'	}' newline



endreport</report>
		<report>Report '_startState'

'
	//Starting point
	State currentState = State.' 

foreach .StartState
{
	do ~From&gt;NamelessTransition~To.()
	{
		subreport '_stateName' run
	}
}

';' newline

'
	@TestSuiteField
	private TestSuite history = new TestSuite();
	  
	@BeforeSuite
	public void beforeSuite(){
		System.out.println("Test suite starts...");
	}
	
	@BeforeTest
	public void beforeTest(){
		System.out.println("\nTest case "+(history.getAllTestCases().size()+1)+" start");
		currentState = State.' 
foreach .StartState
{
	do ~From&gt;NamelessTransition~To.()
	{
		subreport '_stateName' run
	}
}
';
	}

	@AfterTest
	public void afterTest(){
		System.out.println("Test case ends");
	}

	@BeforeSuite
	public void afterSuite(){
		System.out.println("Test suite ends");
	}
'

endreport</report>
		<report>Report '_StateEnumList'

/*Skip the generation setup, start state and end state*/
foreach .() where type &lt;&gt; 'GenerationSetup' and type &lt;&gt; 'StartState' and type &lt;&gt; 'EndState'{
	'		'subreport '_stateName' run ','newline
}

endreport</report>
		<report>Report '_statemachine'


/*Model starting point*/
subreport '_startState' run

foreach &gt;Transition
{
	if not ~To.EndState; then
		subreport '_guard' run
		subreport '_transition' run
	endif
}

/*If end state exist*/
subreport '_endstates' run

endreport</report>
		<report>Report '_stateName'

if :Name = '' then
	oid
else
	:Name%var
endif

endreport</report>
		<report>Report '_testGenerationSetup'

subreport '_imports' run
'
/*
* This is automatically generated OSMO model
* Generated model author: Olli-Pekka Puolitaival
*/
public class ':Name;-1;'_':Name%var' {
'

newline
'	public static void main(String[] args) {' newline		
'		OSMOTester tester = new OSMOTester();' newline
'		tester.addModelObject(new ':Name;-1;'());' newline
'		tester.setAlgorithm(new ':Algorithm'());' newline

do :TestCaseEndCondition{
	'		tester.addTestEndCondition(new ' subreport '_endCondition' run ');' newline
}

do :TestSuiteEndCondition{
	'		tester.addSuiteEndCondition(new ' subreport '_endCondition' run ');' newline
}
'		tester.generate();
	}
}' newline

endreport</report>
		<report>Report '_transition'

	/*Annoitation, 10 = default value*/
	if :weight &lt;&gt; '' then
		'	@Transition(name="'subreport '_transitionName' run'", weight=':weight')'newline
	else
		'	@Transition("'subreport '_transitionName' run'")'newline
	endif
	
	/*Function header*/
	'		public void Transition_'subreport '_transitionName' run'(){' newline
	
	/*Printing is the action in this language*/
	'		System.out.println("':Name'");' newline

	/*Set current state as it is*/
	'		currentState = State.'do ~To.(){subreport '_stateName' run} ';'newline
	
	/*end function*/
	'	}' newline

endreport</report>
		<report>Report '_transitionName'

if :Name = '' then
	oid
else
	:Name%var oid
endif

endreport</report>
		<constraints>
			<occurrence>
				<object href="#Object_StartState_user_3496916418"></object>
				<max>1</max>
			</occurrence>
		</constraints>
	</graph>
</gxl>