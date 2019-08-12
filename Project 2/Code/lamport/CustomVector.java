package lamport;

public class CustomVector {
	public int vector[];
	private CustomVector(){
		
	}	
	private CustomVector(int val[]){
		vector = new int[val.length];
		for(int i = 0;  i < val.length; i++){
			vector[i] = val[i];
		}
	}
	
	public static CustomVector copy(int val[]){
		return new CustomVector(val);
	}
	
	public static CustomVector copy(String vc){
		String vcs[] = vc.split(",");
		int val[] = new int[vcs.length];
		for(int i = 0; i < val.length; i++){
			val[i] = Integer.parseInt(vcs[i]);
		}
		return new CustomVector(val);
	}
	
	public static int compare(CustomVector big, CustomVector small){
		for(int i = 0; i < big.vector.length; i++){
			if(big.vector[i] > small.vector[i]){
				while(i < big.vector.length){
					if(big.vector[i] < small.vector[i]) 
						return 0;
					i++;
				}
				return 1;
			} else if(big.vector[i] < small.vector[i]){
				while(i < big.vector.length){
					if(big.vector[i] > small.vector[i]) 
						return 0;
					i++;
				}
				return -1;
			}
		}		
		return 0;
	}
	
	public String toString(){
		StringBuilder re = new StringBuilder();		
		for(int i = 0; i < vector.length; i++){
			re.append(vector[i]);
			re.append(" ");			
		}
		return re.substring(0,re.length()-1);
	}
}
