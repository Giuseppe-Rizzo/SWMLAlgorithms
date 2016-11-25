package evaluation.task;

import java.util.Arrays;
import java.util.HashSet;

import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;

import utils.Couple;
import evaluation.AIFBConceptGenerator;
import evaluation.CarcinogesesisGenerator;
import knowledgeBasesHandler.KnowledgeBase;

public class BiblicWomanPrediction  extends ClassMembershipPrediction {


		public BiblicWomanPrediction() {
			// TODO Auto-generated constructor stub
		}

		public BiblicWomanPrediction(KnowledgeBase k) {
			super();
			kb= k;
			String[] positiveExamples = { "Anna",
					"Apphia",
					"Bathsheba",
					"Bernice",
					"Candace",
					"Chloe",
					"Claudia",
					"Damaris",
					"Drusilla",
					"Elizabeth",
					"Eunice",
					"Euodia",
					"Eve",
					"Hagar",
					"Herodias",
					"Jezebel",
					"Joanna",
					"Julia",
					"Junia",
					"Lois",
					"Lydia",
					"Martha",
					"Mary",
					"MaryMagdalene",
					"MaryMotherOfJohnMark",
					"MaryOfBethany",
					"MaryOfRome",
					"MaryWifeOfClopas",
					"Nympha",
					"Persis",
					"Phoebe",
					"Priscilla",
					"Rachel",
					"Rahab",
					"Rebecca",
					"Rhoda",
					"Ruth",
					"Salome",
					"Sapphira",
					"Sarah",
					"Susanna",
					"Syntyche",
					"Tabitha",
					"Tamar",
					"Tryphaena",
					"Tryphosa"
					};

	  String[] negativeExamples = {
			  "Aaron",
			  "Abel",
			  "Abiathar",
			  "AbijahSonOfRehoboam",
			  "AbijahthePriest",
			  "Abiud",
			  "Abraham",
			  "Achaicus",
			  "Achim",
			  "Adam",
			  "Addi",
			  "Admin",
			  "Aeneas",
			  "Agabus",
			  "Ahaz",
			  "Alexander",
			  "AlexanderOfEphesus",
			  "AlexanderOfTheSanhedrin",
			  "AlexanderTheCoppersmith",
			  "AlexanderTheHeretic",
			  "Alphaeus",
			  "AlphaeusFatherOfLevi",
			  "Amminadab",
			  "Amos",
			  "AmosSonOfNahum",
			  "Ampliatus",
			  "Ananias",
			  "AnaniasOfDamascus",
			  "AnaniasTheHighPriest",
			  "Andrew",
			  "Andronicus",
			  "Annas",
			  "Antipas",
			  "Apelles",
			  "Apollos",
			  "Aquila",
			  "Archelaus",
			  "Archippus",
			  "Aretas",
			  "Aristarchus",
			  "Aristobulus",
			  "Arni",
			  "Arphaxad",
			  "Artemas",
			  "Asaph",
			  "Asher",
			  "Asyncritus",
			  "Augustus",
			  "Azor",
			  "Balaam",
			  "Balak",
			  "Bar-Jesus",
			  "Barabbas",
			  "Barachiah",
			  "Barak",
			  "Barnabas",
			  "Bartholomew",
			  "Bartimaeus",
			  "Benjamin",
			  "Beor",
			  "Blastus",
			  "Boaz",
			  "CaesarAugustus",
			  "CaesarNero",
			  "Caiaphas",
			  "Cain",
			  "CainanSonOfArphaxad",
			  "CainanSonOfEnosh",
			  "Caiphas",
			  "Carpus",
			  "Chuza",
			  "Claudius",
			  "ClaudiusLysias",
			  "Clement",
			  "Cleopas",
			  "Cornelius",
			  "Cosam",
			  "Crescens",
			  "Crispus",
			  "Daniel",
			  "David",
			  "Demas",
			  "Demetrius",
			  "DemetriusTheSilversmith",
			  "Dionysius",
			  "Diotrephes",
			  "Eber",
			  "Eleazar",
			  "EliakimSonOfAbiud",
			  "EliakimSonOfMelea",
			  "Eliezer",
			  "Elijah",
			  "Elisha",
			  "Eliud",
			  "Elmadam",
			  "Enoch",
			  "Enos",
			  "Epaenetus",
			  "Epaphras",
			  "Epaphroditus",
			  "Er",
			  "Erastus",
			  "ErastusTheTreasurer",
			  "Esau",
			  "Esli",
			  "Eubulus",
			  "Eutychus",
			  "Felix",
			  "Festus",
			  "Fortunatus",
			  "Gad",
			  "GaiusOfCorinth",
			  "GaiusOfDerbe",
			  "GaiusOfMacedonia",
			  "GaiusTheBeloved",
			  "Gallio",
			  "Gamaliel",
			  "Gideon",
			  "Gog",
			  "Hamor",
			  "Heli",
			  "Hermas",
			  "Hermes",
			  "Hermogenes",
			  "HerodAgrippaI",
			  "HerodAgrippaII",
			  "HerodAntipas",
			  "HerodTheGreat",
			  "Herodion",
			  "Hezekiah",
			  "Hezron",
			  "Hosea",
			  "Hymenaeus",
			  "Immanuel",
			  "Isaac",
			  "Isaiah",
			  "Iscariot",
			  "Issachar"};
	  
	  
	  final int length = (positiveExamples.length)+(negativeExamples.length);
			allExamples= kb.getIndividuals();
			
			OWLIndividual[] newAllExamples = new OWLIndividual[length];
			
			HashSet<OWLIndividual> toUpdate= new  HashSet<OWLIndividual>();
			toUpdate.addAll(Arrays.asList(allExamples));
			HashSet<String> positive= new  HashSet<String>();
			positive.addAll(Arrays.asList(positiveExamples));
			HashSet<String> negative= new  HashSet<String>();
			negative.addAll(Arrays.asList(negativeExamples));
			classification = new int[1][length];
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
//			classification=kb.getClassMembershipResult(testConcepts, negTestConcepts,allExamples);

		}


	}

	
	
	


