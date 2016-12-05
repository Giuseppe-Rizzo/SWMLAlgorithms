package evaluation.task;

import java.util.Arrays;
import java.util.HashSet;

import knowledgeBasesHandler.KnowledgeBase;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;

import utils.Couple;
import evaluation.CarcinogesesisGenerator;

public class MutagenicPrediction extends ClassMembershipPrediction {

	public MutagenicPrediction() {
		// TODO Auto-generated constructor stub
	}

	public MutagenicPrediction(KnowledgeBase k) {
		super();
		kb= k;
		CarcinogesesisGenerator gen= new CarcinogesesisGenerator(kb);
		
		Couple<OWLClassExpression[], OWLClassExpression[]> query= gen.generateQueryConcept();
		
		testConcepts= query.getFirstElement();
		negTestConcepts=query.getSecondElement();
	
		allExamples= gen.getExamples();
		kb.updateExamples(allExamples);
		classification=kb.getClassMembershipResult(testConcepts, negTestConcepts,allExamples);

	}
	
	public MutagenicPrediction(KnowledgeBase k, boolean conceptlearning) {
		super();
		kb= k;
		String[] positiveExamples = { "d1",
	"d10",
	"d101",
	"d102",
	"d103",
	"d106",
	"d107",
	"d108",
	"d11",
	"d12",
	"d13",
	"d134",
	"d135",
	"d136",
	"d138",
	"d140",
	"d141",
	"d144",
	"d145",
	"d146",
	"d147",
	"d15",
	"d17",
	"d19",
	"d192",
	"d193",
	"d195",
	"d196",
	"d197",
	"d198",
	"d199",
	"d2",
	"d20",
	"d200",
	"d201",
	"d202",
	"d203",
	"d204",
	"d205",
	"d21",
	"d22",
	"d226",
	"d227",
	"d228",
	"d229",
	"d231",
	"d232",
	"d234",
	"d236",
	"d239",
	"d23_2",
	"d242",
	"d245",
	"d247",
	"d249",
	"d25",
	"d252",
	"d253",
	"d254",
	"d255",
	"d26",
	"d272",
	"d275",
	"d277",
	"d279",
	"d28",
	"d281",
	"d283",
	"d284",
	"d288",
	"d29",
	"d290",
	"d291",
	"d292",
	"d30",
	"d31",
	"d32",
	"d33",
	"d34",
	"d35",
	"d36",
	"d37",
	"d38",
	"d42",
	"d43",
	"d44",
	"d45",
	"d46",
	"d47",
	"d48",
	"d49",
	"d5",
	"d51",
	"d52",
	"d53",
	"d55",
	"d58",
	"d6",
	"d7",
	"d84",
	"d85_2",
	"d86",
	"d87",
	"d88",
	"d89",
	"d9",
	"d91",
	"d92",
	"d93",
	"d95",
	"d96",
	"d98",
	"d99",
	"d100",
	"d104",
	"d105",
	"d109",
	"d137",
	"d139",
	"d14",
	"d142",
	"d143",
	"d148",
	"d16",
	"d18",
	"d191",
	"d206",
	"d230",
	"d233",
	"d235",
	"d237",
	"d238",
	"d23_1",
	"d24",
	"d240",
	"d241",
	"d243",
	"d244",
	"d246",
	"d248",
	"d250",
	"d251",
	"d27",
	"d273",
	"d274",
	"d278",
	"d286",
	"d289",
	"d3",
	"d39",
	"d4",
	"d40",
	"d41",
	"d50",
	"d54",
	"d56",
	"d57",
	"d8",
	"d85_1",
	"d90",
	"d94",
	"d97",
	"d296",
	"d305",
	"d306",
	"d307",
	"d308",
	"d311",
	"d314",
	"d315",
	"d316",
	"d320",
	"d322",
	"d323",
	"d325",
	"d329",
	"d330",
	"d331",
	"d332",
	"d333",
	"d336",
	"d337"
	};

  String[] negativeExamples = {
				"d110",
				"d111",
				"d114",
				"d116",
				"d117",
				"d119",
				"d121",
				"d123",
				"d124",
				"d125",
				"d127",
				"d128",
				"d130",
				"d133",
				"d150",
				"d151",
				"d154",
				"d155",
				"d156",
				"d159",
				"d160",
				"d161",
				"d162",
				"d163",
				"d164",
				"d165",
				"d166",
				"d169",
				"d170",
				"d171",
				"d172",
				"d173",
				"d174",
				"d178",
				"d179",
				"d180",
				"d181",
				"d183",
				"d184",
				"d185",
				"d186",
				"d188",
				"d190",
				"d194",
				"d207",
				"d208_1",
				"d209",
				"d210",
				"d211",
				"d212",
				"d213",
				"d214",
				"d215",
				"d217",
				"d218",
				"d219",
				"d220",
				"d224",
				"d256",
				"d257",
				"d258",
				"d261",
				"d262",
				"d263",
				"d264",
				"d265",
				"d266",
				"d267",
				"d269",
				"d271",
				"d276",
				"d280",
				"d285",
				"d287",
				"d293",
				"d294",
				"d59",
				"d60",
				"d61",
				"d63",
				"d64",
				"d65",
				"d69",
				"d70",
				"d71",
				"d72",
				"d73",
				"d74",
				"d75",
				"d76",
				"d77",
				"d78",
				"d79",
				"d80",
				"d81",
				"d82",
				"d112",
				"d113",
				"d115",
				"d118",
				"d120",
				"d122",
				"d126",
				"d129",
				"d131",
				"d132",
				"d149",
				"d152",
				"d153",
				"d157",
				"d158",
				"d167",
				"d168",
				"d175",
				"d176",
				"d177",
				"d182",
				"d187",
				"d189",
				"d208_2",
				"d216",
				"d221",
				"d222",
				"d223",
				"d225",
				"d259",
				"d260",
				"d268",
				"d270",
				"d282",
				"d295",
				"d62",
				"d66",
				"d67",
				"d68",
				"d83",
				"d297",
				"d298",
				"d299",
				"d300",
				"d302",
				"d303",
				"d304",
				"d309",
				"d312",
				"d313",
				"d317",
				"d318",
				"d319",
				"d324",
				"d326",
				"d327",
				"d328",
				"d334",
				"d335"
				};
  
  
	
		allExamples= kb.getIndividuals();
		
		OWLIndividual[] newAllExamples = new OWLIndividual[337];
		
		HashSet<OWLIndividual> toUpdate= new  HashSet<OWLIndividual>();
		toUpdate.addAll(Arrays.asList(allExamples));
		HashSet<String> positive= new  HashSet<String>();
		positive.addAll(Arrays.asList(positiveExamples));
		HashSet<String> negative= new  HashSet<String>();
		negative.addAll(Arrays.asList(negativeExamples));
		classification = new int[1][337];
		
		int i=0;
		for (OWLIndividual ind:toUpdate){
			
			if (positive.contains(ind.toString())){
				classification[0][i]=+1;
				newAllExamples[i]=ind;
				i++;
			}
			else if (negative.contains(ind.toString())){
				classification[0][i]=-1;
				newAllExamples[i]=ind;
				i++;
			}
			else{ 
				
				toUpdate.remove(i);
			}
			
		}
		allExamples=newAllExamples;
		System.out.println(allExamples.length);
		kb.updateExamples(allExamples);
		kb.setClassMembershipResult(classification);
//		classification=kb.getClassMembershipResult(testConcepts, negTestConcepts,allExamples);

	}


}
