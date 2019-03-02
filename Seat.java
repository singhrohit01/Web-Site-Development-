package CinemaEbooking.Entity;

public class Seat {

	int seatID;
	
	public Seat(int id){
		seatID = id-1;
	}
	
	public String toString(){
		String name = "" + (seatID/7 + 1);
		name+="-";
		switch(seatID%7){
		case 0:
			name += "A";
			break;
		case 1:
			name += "B";
			break;
		case 2:
			name += "C";
			break;
		case 3:
			name += "D";
			break;
		case 4:
			name += "E";
			break;
		case 5:
			name += "F";
			break;
		case 6:
			name += "G";
			break;
		}
		return name;
	}

}
