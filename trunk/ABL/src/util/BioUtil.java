package util;

import static protein.Constants.aminos;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import mlGui.GuiFeatureConfig;
import mlGui.task.SelfLearnTask;

import org.apache.log4j.Logger;

import protein.FastaInstanceReader;
import util.threading.ProtSeqFeatureExtract;
import util.threading.ProtSeqValid;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import beans.WekaDataBean;
import feature.common.Extractor;
import feature.common.Feature;
import feature.common.FeatureVector;
import feature.common.commonIntrface.FeatureFormatter;
import feature.common.commonIntrface.Instance;
import feature.common.commonIntrface.InstanceReader;
import feature.common.printer.ARFFFormater;
import feature.common.printer.PrinterProvider;
import gui.TrainingDataWindow;


public class BioUtil {
	private static Logger log = Logger.getLogger(BioUtil.class);

	public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
		long start = System.currentTimeMillis();
		/*extractFeatures(new String[]{"Test"},"Name", 
				new String []{"/home/rishi.das/Project/Aim/Models_data/RNApred/RNA-binding_train.fasta"}, 
				"/home/rishi.das/multiSync.libsvm");*/
		filterGoodBad("/home/rishi/Project/Aim/cleverSuite/Structural Disorder/disprot_fasta_v6.02.txt");
		long end = System.currentTimeMillis();
		long elapsedTime = end - start;
		  System.out.println("The process took approximately: "
		  + elapsedTime + " milli seconds");
		  //System.out.println(onlyBasicAminoAcids("XPPPSTLCSYWALEQGKLSQASGLLLVLFSPRVSGR	#$*@LGSVSV!AGVNVEALFPAWPMESKT"));
		  	
	}
	
	private static void filterGoodBad(String pPath) throws FileNotFoundException, IOException, Exception {
		InstanceReader pInstanceReader = new FastaInstanceReader();

		
		pInstanceReader.setSource(pPath);
		
		BufferedWriter lgood = new BufferedWriter(new FileWriter(pPath+"good"));
		BufferedWriter lbad = new BufferedWriter(new FileWriter(pPath+"bad"));

		// This could be utilized to help the user to get rid of error.
//		BufferedWriter lBW = new BufferedWriter(new FileWriter(pInputFilePaths[i]+".cured.faa"));
		Instance lInstance;
		int bad=0;
		while ((lInstance =pInstanceReader.nextInstance()) != null) {
			ProtSeqValid l = new ProtSeqValid(lInstance);
			if(l.call()==null){
				lgood.write(fasta(lInstance));
			}else{
				
				bad++;
				lbad.write(fasta(lInstance));
			}
			
		}
		System.out.println(bad+" bad sequences found");
		lgood.close();
		lbad.close();
	}
	
	private static String fasta(Instance lInstance) {
		
		return ">"+lInstance.getInstanceId()+"\n"+lInstance.getInstanceData()+"\n";
	}
	
	public static String isValidFastaByThread(String pFilePath) throws FileNotFoundException, IOException, Exception{

		ExecutorService pool = Executors.newFixedThreadPool(SystemUtil.getNumberOfThreads());
		CompletionService<String> compService = new ExecutorCompletionService<String>(pool);

		InstanceReader pInstanceReader = new FastaInstanceReader();
		StringBuilder lSB = new StringBuilder();
		
		pInstanceReader.setSource(pFilePath);

		/*Instance lInstance;
		int workItems=0;
		while ((lInstance =pInstanceReader.nextInstance()) != null) {
			compService.submit(new ProtSeqValid(lInstance));
			workItems++;
		}
		
		
		// Consume results as they complete (this would typically occur on a different thread).
		for (int i=0; i<workItems; ++i) {
		  Future<String> fut = compService.take(); // Will block until a result is available.
		  String result = fut.get(); // Extract result; this will not block.
		  if(result!=null){
			  lSB.append(result+"\n");
		  }
		}
		
		shutdownAndAwaitTermination(pool);*/
		
		if (pInstanceReader.hasError() || lSB.length()>0) {
			System.err.println("There are error in input files");
			System.err.println(pInstanceReader.getError());
			System.err.println(lSB.toString());
			log.error(pInstanceReader.getError()+lSB.toString());
			String lErrMsg = pInstanceReader.getError().substring(
					pInstanceReader.getError().indexOf("\n", 1) + 1);
			return new String (lErrMsg+lSB.toString());
		}
		

		return null;
	
		
	}
	
	public static void shutdownAndAwaitTermination(ExecutorService pool) {
		   pool.shutdown(); // Disable new tasks from being submitted
		   try {
		     // Wait a while for existing tasks to terminate
		     if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
		       pool.shutdownNow(); // Cancel currently executing tasks
		       // Wait a while for tasks to respond to being cancelled
		       if (!pool.awaitTermination(60, TimeUnit.SECONDS))
		           System.err.println("Pool did not terminate");
		     }
		   } catch (InterruptedException ie) {
		     // (Re-)Cancel if current thread also interrupted
		     pool.shutdownNow();
		     // Preserve interrupt status
		     Thread.currentThread().interrupt();
		   }
		 }


	
	public static String isValidFasta(String pFilePath) throws FileNotFoundException, IOException, Exception{

		InstanceReader pInstanceReader = new FastaInstanceReader();

		
			pInstanceReader.setSource(pFilePath);

			// This could be utilized to help the user to get rid of error.
//			BufferedWriter lBW = new BufferedWriter(new FileWriter(pInputFilePaths[i]+".cured.faa"));
			Instance lInstance;
			while ((lInstance =pInstanceReader.nextInstance()) != null) {
				new ProtSeqValid(lInstance).call();
			}
//			lBW.close();
			if (pInstanceReader.hasError()) {
				System.err.println("There are error in input files");
				System.err.println(pInstanceReader.getError());
				log.error(pInstanceReader.getError());
				String lErrMsg = pInstanceReader.getError().substring(
						pInstanceReader.getError().indexOf("\n", 1) + 1);
				return new String ("Problem in following proteins\n" + lErrMsg);
			}
		

		return null;
	
		
	}

	/**
	 * @param pLabels
	 * @param pRelationName
	 * @param pFilePaths
	 * @param pArffPath
	 * @return
	 */
	public static WekaDataBean extractFeatures(String[] pLabels, String pRelationName, String[] pFilePaths, String pArffPath) {
		GuiFeatureConfig lConfig = new GuiFeatureConfig("/config/protein/FeatureExtractor.xml",
				"/config/protein/ConfigBeans.xml");
				InstanceReader pInstanceReader = new FastaInstanceReader();
				try {
					Extractor lExt = new Extractor(lConfig);
					ARFFFormater lFP = (ARFFFormater) PrinterProvider.getPrinter("arff", pArffPath);
					StringBuilder lSB = new StringBuilder();
					lSB.append("{"+pLabels[0].trim());
					for (int i = 1; i < pLabels.length; i++) {
						lSB.append(","+pLabels[i].trim());
					}
					lSB.append("}");
//					lFP.setMClass("{"+pLabels[0].trim()+","+pLabels[1].trim()+"}");
					lFP.setMClass(lSB.toString());
//					lFP.setMClass("{"+pLabels[0].trim()+"}");
					lFP.setMRelation(pRelationName);
					List<String> lInstanceIds = new ArrayList<String>();
					// TODO set relation name
					for (int i = 0; i < pFilePaths.length; i++) {
						pInstanceReader.setSource(pFilePaths[i]);
						System.out.println("Processing "+pFilePaths[i]+" with label "+pLabels[i]);
						lInstanceIds.addAll(executeFile(pInstanceReader, lExt, pLabels[i].trim(), lFP));
					}

					if(pInstanceReader.hasError()){
						log.error(pInstanceReader.getError());
					}

					DataSource source = new DataSource(pArffPath);
					Instances train = source.getDataSet();
					train.setClassIndex(train.numAttributes()-1);

					WekaDataBean l = new WekaDataBean();
					l.setInst(train);
					l.setInstanceId(lInstanceIds);
					return l;
				} catch (IOException e) {
					log.error("Fatal IOException ",e);
					SystemUtil.showTraingErrMsg(" "+e);
				} catch (Exception e) {
					log.error("Fatal Exception ",e);
					SystemUtil.showTraingErrMsg(" "+e);
				} catch (Error e) {
					log.error("Fatal Error ",e);
					SystemUtil.showTraingErrMsg(" "+e.getMessage());
				}
				
				return null;
	}

	/**
	 * Calculated features are stored in feature formatter.
	 * Instance(protein) ids are returned in a list.  
	 * @param pInstanceReader
	 * @param pExt
	 * @param pClassLabel
	 * @param pFP
	 * @return
	 * @throws Exception
	 */
	private static List<String> executeFile(InstanceReader pInstanceReader,
			Extractor pExt, String pClassLabel, FeatureFormatter pFP)
			throws Exception {
		Instance lInstance;
		String i = "";
		int numOfFeature = 0;
		List<String> l = new ArrayList<String>();
		int sequenceCounter=0;
		try {
			while ((lInstance = pInstanceReader.nextInstance()) != null) {
				i=lInstance.getInstanceId();
				FeatureVector lFV = new FeatureVector(
						lInstance.getInstanceId(), pClassLabel);
				String basicAminoSequence = onlyBasicAminoAcids(((String)lInstance.getInstanceData()).trim());
				lFV.addAll(pExt.extract(basicAminoSequence));
				l.add(lInstance.getInstanceId().replaceAll(",", ""));
				
				
				if (numOfFeature == 0) {
					numOfFeature = lFV.size();
				} else if (numOfFeature != lFV.size()) {
					log.error(i + " " + lInstance.getInstanceId());
					throw new Error("Mismatch number of features");
				}
				pFP.printVector(lFV);
				if((++sequenceCounter)%5==0){
					SelfLearnTask.flashMessage((sequenceCounter)+" "+pClassLabel+" proteins processed");
				}
			}
			if (pFP.isHasError()) {
				log.error("Error found in generated attributes for Instances of"
						+ pInstanceReader.getSource());
				for (String id : pFP.getErrorInstanceID()) {
					log.error(id);
				}
			}
		} catch (IOException e) {
			log.error("IOException in input resource, after processing " + i
					+ " instances" );
			throw new Error("Error found after processing " + i	+ " instances" + e);
		} catch (Exception e) {
			log.error("Exception in input resource, after processing " + i
					+ " instances" );
			throw new Error("Error found after processing " + i	+ " instances" );
		}catch (Error e) {
			log.error("Error in input resource, after processing " + i
					+ " instances" + e);
			throw new Error("Error found after processing " + i	+ " instances" );
		}
		
		
		return l;
	}
	
	private static String onlyBasicAminoAcids(String rawAminoAcidSequence) {
		char[] seq = rawAminoAcidSequence.toCharArray();
		StringBuilder lSB = new StringBuilder();
		
		for (char c : seq) {
			if(Arrays.binarySearch(aminos, c + "")<0){
				//error;
			}else{
				lSB.append(c);
			}
		}
		
		return lSB.toString();
	}

	/**
	 * Use threads
	 * Calculated features are stored in feature formatter.
	 * Instance(protein) ids are returned in a list.  
	 * @param pInstanceReader
	 * @param pExt
	 * @param pClassLabel
	 * @param pFP
	 * @return
	 * @throws Exception
	 */
	private static List<String> executeFileByThread(InstanceReader pInstanceReader,
			Extractor pExt, String pClassLabel, FeatureFormatter pFP)
			throws Exception {


		ExecutorService pool = Executors.newFixedThreadPool(SystemUtil.getNumberOfThreads());
		CompletionService<List<Feature>> compService = new ExecutorCompletionService<List<Feature>>(pool);

		Instance lInstance;
		String i = "";
		int numOfFeature = 0;
		List<String> l = new ArrayList<String>();
		int workItems=0;
		List<ProtSeqFeatureExtract> lJobs = new ArrayList<ProtSeqFeatureExtract>();
		while ((lInstance =pInstanceReader.nextInstance()) != null) {
			l.add(lInstance.getInstanceId().replaceAll(",", ""));
			lJobs.add(new ProtSeqFeatureExtract(pExt,lInstance));
			
			workItems++;
		}
		
		//CHECK for other than 20 amino acid fix in non thread version
		for (ProtSeqFeatureExtract protSeqFeatureExtract : lJobs) {
			compService.submit(protSeqFeatureExtract);
		}
		pool.shutdown();
		
		System.out.println("All jobs are Submitted ");
		
		while(!pool.awaitTermination(60, TimeUnit.SECONDS));{
		
			System.out.println("Waiting");
		}
		
		//System.out.println("waitLoop  "+waitLoop);*/
		
		// Consume results as they complete (this would typically occur on a different thread).
		for (ProtSeqFeatureExtract protSeqFeatureExtract : lJobs) {
			
			FeatureVector lFV = new FeatureVector(
					protSeqFeatureExtract.getInstanceId(), pClassLabel);
			 // Will block until a result is available.
			   
			lFV.addAll(protSeqFeatureExtract.getFeatureList()); // Extract result; this will not block.

			if (numOfFeature == 0) {
				numOfFeature = lFV.size();
			} else if (numOfFeature != lFV.size()) {
				log.error(i + " " + protSeqFeatureExtract.getInstanceId());
				System.exit(0);
			}
			pFP.printVector(lFV);
		 
		  
		}
		
		shutdownAndAwaitTermination(pool);
	
		System.out.println("$$$$ Feature Extraction Done !!!");
		return l;
	}

	public static void consoleSummary(Evaluation mResult) throws Exception {
		System.out.println(mResult.toSummaryString(true));
		System.out.println(mResult.toClassDetailsString());
		System.out.println(mResult.confusionMatrix());
		
	}
}

