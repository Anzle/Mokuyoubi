
public class main{

	public static void main(String[] args) {
	
	if(args.length!=2){
		System.out.println("THERE WAS AN ERROR WITH THE INPUTS");
		return;
	}
	
	String tfile=""; // .torrent file to be loaded 
	String sfile=""; // name of the file to save the data to
		
	for(int i=0;i<args.length;i++){
		if(i==0){
			tfile=args[i];
		}else if(i==1){
			sfile=args[i];
		}
	}

//the following is a check to make sure the command line arguments were stored correctly
	System.out.println("tfile: "+tfile);  
	System.out.println("sfile: "+sfile);
	
	
	
	
	
	
	}

}
