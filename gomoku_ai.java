// 5.19 2:20

package gomoku_ai;
import java.util.Scanner;

public class gomoku_ai {

    static final int MAX = 15; // 판의 크기
    static int game_num = 1; //game의 진행상황을 나타내는 변수, tree를 사용하지 않아 depth 대신 사용했다.
    static int game_over = 0; //game이 끝났는지를 판단함, check_game 함수에서 사용
    static boolean ifwin = false;
    static boolean first_turn = false;
    static int[][] cell = {
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};
    static Node MainCell = new Node(cell);
    public static void main(String[] args) {
                
        boolean player = true; //player의 차례인지, 컴퓨터의 차례인지를 판단하기 위한 변수
        
        int ii,jj;
        
        //처음 탐색범위는 가운데 3*3이다.
        MainCell.getPosition()[0] = 7;
        MainCell.getPosition()[1] = 7;

        do
        {
          //  grid_print(vcell); //판을 출력
            
                 //player_move
            if(player) {
                
                move_pyr(MainCell);//사용자 입력 받기, position을 받음
                
                if(first_turn == true&&game_num==1){

                }
                else {
                MainCell.setCellPoint(MainCell.getPosition()[0],MainCell.getPosition()[1],-1);
                //룰 체크
                        game_num ++; //룰을 어기지 않았다면 game_num을 진행시킨다.
                }

            }       
            
            //computer_move
            else{

                //tss
            	Node TSSroot = new Node(MainCell.getCell());
                TSSroot.getPosition()[0] = MainCell.getPosition()[0];
                TSSroot.getPosition()[1] = MainCell.getPosition()[1];               
            
                ifwin = TSS(TSSroot,5,false);
                
                //상대방 threat가 있을 땐 방어
                Node checknode = new Node(MainCell.getCell());
                checknode.getPosition()[0] = MainCell.getPosition()[0];
                checknode.getPosition()[1] = MainCell.getPosition()[1];
                
                int checkint= threatCheck2(checknode,true);
                if(checkint!=0){
                	
                    ii = checknode.costSquares.get(0).getX();
                    jj = checknode.costSquares.get(0).getY();
                    
                    MainCell.setCellPoint(ii,jj, 1);
                    MainCell.setPosition(ii,jj);
                    game_num ++; //게임을 한단계 더진행함

                    ifwin = false;
                }
                //상대방 threat가 없으면
                else{
                
                if(ifwin){
                    //Maincell.costSquares를 TSSroot의 cost square로 업데이트한다.
                    MainCell.setCellPoint(TSSroot.getBestPosition()[0], TSSroot.getBestPosition()[1], 1);
                    MainCell.setPosition(TSSroot.getBestPosition()[0], TSSroot.getBestPosition()[1]);
                    game_num ++; //게임을 한단계 더진행함
                    
                }
                
                else {
                    //트리 만들기위한 root 생성
                    Node root = new Node(MainCell.getCell());
                    root.getPosition()[0] = MainCell.getPosition()[0];
                    root.getPosition()[1] = MainCell.getPosition()[1];
                  
                    //alpha-beta
                    int value = alphabeta(root,2,-99999,99999,true); //root에 i, j값을 저장할 예정
                    // root의 bestposition으로 돌을 두고, 업데이트한다.
                    MainCell.setCellPoint(root.getBestPosition()[0], root.getBestPosition()[1], 1);
                    MainCell.setPosition(root.getBestPosition()[0], root.getBestPosition()[1]);
                    game_num ++; //게임을 한단계 더진행함
                    
                }
                }
                print_exe();

            }
            player = !player; //차례를 바꾸고
                        
        }while(game_num<226);

    }
    
    
    public static boolean TSS(Node parentNode, int depth, boolean pyr){
        
        int i,j;
        int tempint = 0;
        //종료조건
        if(depth == 0){
            return false;
        }
        
        
        //create tree part
        if(!pyr){
            int tempi = MainCell.getPosition()[0];
            int tempj = MainCell.getPosition()[1];
            
            for( i= 0; i<MAX;i++){for(j=0;j<MAX;j++){
                    if(parentNode.getPoint(i,j) == 0){
                        //child node 생성
                        Node childNode = new Node(parentNode.getCell());
                        childNode.setCellPoint(i,j,1);
                        childNode.setPosition(i,j);
                        
                        //renjurule을 어기지 않았을 때만 검사한다.
                        if(!renjurule(childNode,pyr)){

                            tempint = threatCheck(childNode,pyr);
         //                   System.out.println(tempint+": CHECK"+i+","+j+"depth :"+ depth);
                            if (tempint >= 2) {
          //                      System.out.println("winning"+i+","+j+"depth :"+ depth);

                                parentNode.setBestPosition(childNode.getPosition()[0],childNode.getPosition()[1]);
                                for (int l = 0; l< childNode.costSquares.size();l++){
                                    MyObject tempArr2 = new MyObject(childNode.costSquares.get(l).getX(),childNode.costSquares.get(l).getY());
                                    parentNode.costSquares.add(tempArr2);
                                }
                                return true;
                            
                            }
                            else if (tempint ==1) {
       //                         System.out.println("TSS child 생성"+i+","+j+"depth :"+ depth);
                                Tree.add(parentNode, childNode);    
                            }
                        }
                    }
                }
            }
        }
        //pyr
        else{
            
            //cost square에 넣고 tree에 add하기.
            for(int k=0; k<parentNode.costSquares.size();k++){
                i = parentNode.costSquares.get(k).getX();
                j = parentNode.costSquares.get(k).getY();

                //search space가 0인 경우, 아직 비어있는 경우
                if (parentNode.getPoint(i,j) ==0){
                    Node childNode = new Node(parentNode.getCell());
                    childNode.setCellPoint(i,j,-1);
                    childNode.setPosition(i,j);
                    //삼삼 룰을 어기지 않을 때 노드를 추가한다.
                    if(!renjurule(childNode,pyr)){
                        tempint = threatCheck(childNode,pyr);
          //              System.out.println("TSS child 생성"+i+","+j+"depth :"+ depth);
                        
                        if(tempint>=2) return false;
                        //수비자가 이기게 될 경우는 두지 않는다.
                        Tree.add(parentNode, childNode);
                    }
                }
            }               
            
        }
        
        
        //recursive call
        
        if(!pyr){
            Node tempChild = parentNode.getLeftChild();
            while(tempChild != null){
                boolean temp1 = TSS(tempChild,depth-1,true);
                if (temp1 == true){
                    parentNode.setBestPosition(tempChild.getPosition()[0],tempChild.getPosition()[1]);
                    //cost square update
                    
                    for (int l = 0; l< tempChild.costSquares.size();l++){
                        MyObject tempArr2 = new MyObject(tempChild.costSquares.get(l).getX(),tempChild.costSquares.get(l).getY());
                        parentNode.costSquares.add(tempArr2);
                    }
                    
                    return true;
                }
                tempChild = tempChild.getRightSibling();
            }
        }else{
            
            Node tempChild = parentNode.getLeftChild();
            while(tempChild != null){
                boolean temp2 = TSS(tempChild,depth-1,false);
                if (temp2 == true){
                    return true;
                }
                tempChild = tempChild.getRightSibling();
            }
        }
        
        return false;
    }
    
    //alpha-beta pruning이다.
    public static int alphabeta(Node parentNode, int depth, int a, int b, boolean Maxing){
        //결국 parent Node의 가장 값이 큰 노드의 cell 혹은 position을 전달해야 함.
        
        int i, j;
        int x;
        if (Maxing) x=1; else x=-1;
        
// 종료조건을 체크하여 heuristic evaluation 함수를 실행시킨다.
        if (depth == 0){ 
            return heurisitc(parentNode,depth);
        }
        
// create tree part
        int tempi = MainCell.getPosition()[0];
        int tempj = MainCell.getPosition()[1];
        for (i = tempi - 2; i<=tempi + 2; i++){
            for(j=tempj - 2; j<=tempj +2; j++){

        		if(parentNode.getPoint(i,j) == 0){
                    //child node 생성
                    Node childNode = new Node(parentNode.getCell());
                    childNode.setCellPoint(i,j,x);
                    childNode.setPosition(i,j);
                    //renjurule을 어기지 않았을 때만 검사한다.
                    if(!renjurule(childNode,!Maxing)){
                        Tree.add(parentNode, childNode);
                    }
                }
            }
        }
        if (game_num>10){
        if (parentNode.getLeftChild() == null){
            for (i = 0; i<MAX;i++){
                for(j=0; j<MAX; j++){
                    if(parentNode.getPoint(i,j) == 0){
                        //child node 생성
                        Node childNode = new Node(parentNode.getCell());
                        childNode.setCellPoint(i,j,x);
                        childNode.setPosition(i,j);
                        //renjurule을 어기지 않았을 때만 검사한다.
                        if(!renjurule(childNode,!Maxing)){
                            Tree.add(parentNode, childNode);
                        }
                    }
                }
            }
        }
        }
        
        //alpha-beta value evaluation
                
        if (Maxing){
            Node tempChild = parentNode.getLeftChild();
            while(tempChild != null) {
                int temp1 = alphabeta(tempChild,depth-1,a,b,false);
          //      System.out.println("comp d : "+ depth +"temp1 : "+temp1+" : position i, j : "+tempChild.getPosition()[0]+" ,"+tempChild.getPosition()[1]);
                if(a<temp1){
                    a = temp1;
                    parentNode.setBestPosition(tempChild.getPosition()[0],tempChild.getPosition()[1]);
         //           System.out.println("changes d : "+ depth + " " + a+ " : position i, j : "+parentNode.getBestPosition()[0]+" ,"+parentNode.getBestPosition()[1]);
                }
                //pruning
                if (b<=a){
                //  System.out.println("alpha cut");
                    break;
                }
                
                tempChild = tempChild.getRightSibling();
            }
    //      System.out.println("comp d : "+depth+ " v : "+ a + " i,j : "+ parentNode.getBestPosition()[0]+" ,"+parentNode.getBestPosition()[1]);
            return a;
        }
        else{
            Node tempChild = parentNode.getLeftChild();
            while(tempChild != null) {

                int temp2 = alphabeta(tempChild,depth-1,a,b,true);
     //           System.out.println("pyr d : "+ depth + " temp : "+ temp2 +" : position i, j : "+tempChild.getPosition()[0]+" ,"+tempChild.getPosition()[1]);

                if (b>temp2){
                    b = temp2;
                    //parentNode.setPosition(tempChild.getPosition()[0], tempChild.getPosition()[1]);
                    parentNode.setBestPosition(tempChild.getPosition()[0],tempChild.getPosition()[1]);
                    
     //                   System.out.println("pyr d : "+depth+ " v : "+b + " i,j : "+ parentNode.getBestPosition()[0]+" ,"+parentNode.getBestPosition()[1]);
                }
                
                //update beta

                //pruning
                if (b<=a){
            //      System.out.println("beta cut");
                    break;
                }
                
                tempChild = tempChild.getRightSibling();
            }
        //  System.out.println("pyr d : "+depth+ " v : "+b + " i,j : "+ parentNode.getBestPosition()[0]+" ,"+parentNode.getBestPosition()[1]);
            return b;
        }
    }
    
    public static int heurisitc(Node O, int num){
        int result = 0;
        int tempi = O.getPosition()[0];
        int tempj = O.getPosition()[1];
        
        //thraet
      result += heuristicValue1(O,num)*5;
    
      //2목
      for(int i = tempi-2; i<tempj+2; i++){
            for(int j = tempi-2; j<tempj+2; j++){
                
                O.setPosition(i, j);
                if(O.getPoint(i, j) != 0){
                    result += heuristicValue2(O,num);
                }
            }
        }
      O.setPosition(tempi, tempj);
        return result;
    }
    
    //3 이상을 판단하는 함수(공격 수비)
    
    public static int heuristicValue1(Node O, int num){
        int result = 0;
        
        int x = O.getPosition()[0];
        int y = O.getPosition()[1];

        ///////pyr threat//////
        
        //OOOOO
        if (//row
                (O.getPoint(x-1,y)!=-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)==-1&&O.getPoint(x+5,y)!=-1)
                ||(O.getPoint(x-2,y)!=-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)!=-1)
                ||(O.getPoint(x-3,y)!=-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)!=-1)
                ||(O.getPoint(x-4,y)!=-1&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)!=-1)
                ||(O.getPoint(x-5,y)!=-1&&O.getPoint(x-4,y)==-1&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)!=-1)
    //col
                ||(O.getPoint(x,y-1)!=-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)==-1&&O.getPoint(x,y+5)!=-1)
                ||(O.getPoint(x,y-2)!=-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)!=-1)
                ||(O.getPoint(x,y-3)!=-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)!=-1)
                ||(O.getPoint(x,y-4)!=-1&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)!=-1)
                ||(O.getPoint(x,y-5)!=-1&&O.getPoint(x,y-4)==-1&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)!=-1)
    //diag
                ||(O.getPoint(x-1,y+1)!=-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)==-1&&O.getPoint(x+5,y-5)!=-1)
                ||(O.getPoint(x-2,y+2)!=-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)!=-1)
                ||(O.getPoint(x-3,y+3)!=-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)!=-1)
                ||(O.getPoint(x-4,y+4)!=-1&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)!=-1)
                ||(O.getPoint(x-5,y+5)!=-1&&O.getPoint(x-4,y+4)==-1&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)!=-1)
    //diag      
                ||(O.getPoint(x-1,y-1)!=-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)==-1&&O.getPoint(x+5,y+5)!=-1)
                ||(O.getPoint(x-2,y-2)!=-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)!=-1)
                ||(O.getPoint(x-3,y-3)!=-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)!=-1)
                ||(O.getPoint(x-4,y-4)!=-1&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)!=-1)
                ||(O.getPoint(x-5,y-5)!=-1&&O.getPoint(x-4,y-4)==-1&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)!=-1)

                ){
            result = -5000;
            return result;
        }
//OOOXO pyr, 1,1,1,0,1 
        
        if ((O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==0&&O.getPoint(x+4,y)==-1)
        ||(O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==0&&O.getPoint(x+3,y)==-1)
        ||(O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==-1)
        ||(O.getPoint(x-4,y)==-1&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1)){
            return -800;
        }
        
        if ((O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==0&&O.getPoint(x,y+4)==-1)
        ||(O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==0&&O.getPoint(x,y+3)==-1)
        ||(O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==-1)
        ||(O.getPoint(x,y-4)==-1&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1)){
            
                return -800;
            }
        if ((O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==0&&O.getPoint(x+4,y-4)==-1)
        ||(O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==0&&O.getPoint(x+3,y-3)==-1)
        ||(O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==-1)
        ||(O.getPoint(x-4,y+4)==-1&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1)){
            return -800;
            }
        
        if ((O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==0&&O.getPoint(x+4,y+4)==-1)
        ||(O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==0&&O.getPoint(x+3,y+3)==-1)
        ||(O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==-1)
        ||(O.getPoint(x-4,y-4)==-1&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1)){
            return -800;
        }
        //.OOXOO.   comp
        
        if ((O.getPoint(x-1, y)!=-1&&O.getPoint(x+5, y)!=-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==0&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)==-1)
        || (O.getPoint(x-2, y)!=-1&&O.getPoint(x+4, y)!=-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==-1)
        ||(O.getPoint(x-4, y)!=-1&&O.getPoint(x+2, y)!=-1&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1)
        ||(O.getPoint(x-5, y)!=-1&&O.getPoint(x+1, y)!=-1&&O.getPoint(x-4,y)==-1&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1)
        
        || (O.getPoint(x, y-1)!=-1&&O.getPoint(x, y+5)!=-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==0&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)==-1)
        ||(O.getPoint(x, y-2)!=-1&&O.getPoint(x, y+4)!=-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==-1)
        ||(O.getPoint(x, y-4)!=-1&&O.getPoint(x, y+2)!=-1&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1)
        ||(O.getPoint(x, y-5)!=-1&&O.getPoint(x, y+1)!=-1&&O.getPoint(x,y-4)==-1&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1)


        ||(O.getPoint(x-1, y+1)!=-1&&O.getPoint(x+5, y-5)!=-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==0&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)==-1)
        ||(O.getPoint(x-2, y+2)!=-1&&O.getPoint(x+4, y-4)!=-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==-1)
        ||(O.getPoint(x-4, y+4)!=-1&&O.getPoint(x+2, y-2)!=-1&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1)
        ||(O.getPoint(x-5, y+5)!=-1&&O.getPoint(x+1, y-1)!=-1&&O.getPoint(x-4,y+4)==-1&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1)
        
        ||(O.getPoint(x-1, y-1)!=-1&&O.getPoint(x+5, y+5)!=-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==0&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)==-1)
        ||(O.getPoint(x-2, y-2)!=-1&&O.getPoint(x+4, y+4)!=-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==-1)
        ||(O.getPoint(x-4, y-4)!=-1&&O.getPoint(x+2, y+2)!=-1&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1)
        ||(O.getPoint(x-5, y-5)!=-1&&O.getPoint(x+1, y+1)!=-1&&O.getPoint(x-4,y-4)==-1&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1)){
            
            return -800;
        }
//OXOOO comp, 1,0,1,1,1 
                

        if ((O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)==-1)
            ||(O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1)
            ||(O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1)
            ||(O.getPoint(x-4,y)==-1&&O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1)
                
            ||(O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)==-1)
            ||(O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1)
            ||(O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1)
            ||(O.getPoint(x,y-4)==-1&&O.getPoint(x,y-3)==0&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1)
            
            ||(O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)==-1)
            ||(O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-2)==-1&&O.getPoint(x+2,y-2)==-1)
            ||(O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1)
            ||(O.getPoint(x-4,y+4)==-1&&O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1)
            ||(O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)==-1)
            
            ||(O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1)
                ||(O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1)
                ||(O.getPoint(x-4,y-4)==-1&&O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1)){
            
            return -800;
        }
            
        //●OOOOX 6 pyr blocked four
        
        if ((O.getPoint(x-1,y)==1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)==0)
            ||(O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==0)
            ||(O.getPoint(x-3,y)==1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==0)
            ||(O.getPoint(x-4,y)==1&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0)){
            result -= 1500 ;
             
        }
        if ((O.getPoint(x,y-1)==1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)==0)
            ||(O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==0)
            ||(O.getPoint(x,y-3)==1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==0)
            ||(O.getPoint(x,y-4)==1&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0)){
            result -= 1500 ;
             
        }
        if ((O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)==0)
            ||(O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==0)
            ||(O.getPoint(x-3,y+3)==1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==0)
            ||(O.getPoint(x-4,y+4)==1&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0)){
            result -= 1500 ;
             
        }
        if ((O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)==0)
            ||(O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==0)
            ||(O.getPoint(x-3,y-3)==1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==0)
            ||(O.getPoint(x-4,y-4)==1&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0)){
            result -= 1500 ;
             
        }
        
        // XOOOOX unblocked four pyr
        if ((O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)==0)
                ||(O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==0)
                ||(O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==0)
                ||(O.getPoint(x-4,y)==0&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0)){
                result -= 4000 ;
                 
            }
            if ((O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)==0)
                ||(O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==0)
                ||(O.getPoint(x,y-3)==0&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==0)
                ||(O.getPoint(x,y-4)==0&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0)){
                result -= 4000 ;
                 
            }
            if ((O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)==0)
                ||(O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==0)
                ||(O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==0)
                ||(O.getPoint(x-4,y+4)==0&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0)){
                result -= 4000 ;
                 
            }
            if ((O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)==0)
                ||(O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==0)
                ||(O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==0)
                ||(O.getPoint(x-4,y-4)==0&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0)){
                result -= 4000 ;
                 
            }

        
        //XOOOO● 6 pyr blocked four

        if ((O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)==1)
            ||(O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==1)
            ||(O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==1)
            ||(O.getPoint(x-4,y)==0&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==1)){
            result -= 1500 ;
             
        }
        if ((O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)==1)
            ||(O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==1)
            ||(O.getPoint(x,y-3)==0&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==1)
            ||(O.getPoint(x,y-4)==0&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==1)){
            result -= 1500 ;
             
        }
        if ((O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)==1)
            ||(O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==1)
            ||(O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==1)
            ||(O.getPoint(x-4,y+4)==0&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==1)){
            result -= 1500 ;
             
        }
        if ((O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)==1)
            ||(O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==1)
            ||(O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==1)
            ||(O.getPoint(x-4,y-4)==0&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==1)){
            result -= 1500 ;
             
        }
        
        //XOOOX 5 pyr three

        if ((O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==0)
            ||(O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==0)
            ||(O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0)){
            result -= 1000 ;
             
        }
        if ((O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==0)
                ||(O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==0)
                ||(O.getPoint(x,y-3)==0&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0)){
                result -= 1000 ;
                 
        }
        if ((O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==0)
                ||(O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==0)
                ||(O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0)){
                result -= 1000 ;
                 
        }
        if ((O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==0)
                ||(O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==0)
                ||(O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0)){
                result -= 1000 ;
                 
        }
    
        //XOXOOX 6 pyr broken three
        if ((O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)==0)
            ||(O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==0)
            ||(O.getPoint(x-4,y)==0&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0)){
            result -= 500 ;
             
        }
        if ((O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)==0)
            ||(O.getPoint(x,y-3)==0&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==0)
            ||(O.getPoint(x,y-4)==0&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0)){
            result -= 500 ;
             
        }
        if ((O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)==0)
            ||(O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==0)
            ||(O.getPoint(x-4,y+4)==0&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0)){
            result -= 500 ;
             
        }
        if ((O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)==0)
            ||(O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==0)
            ||(O.getPoint(x-4,y-4)==0&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0)){
            result -= 500 ;
             
        }
        
        //XOOXOX 6 pyr broken three
        if ((O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==0&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)==0)
            ||(O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==0)
            ||(O.getPoint(x-4,y)==0&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0)){
            result -= 500 ;
             
        }
        if ((O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==0&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)==0)
            ||(O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==0)
            ||(O.getPoint(x,y-4)==0&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0)){
            result -= 500 ;
             
        } 
        if ((O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==0&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)==0)
            ||(O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==0)
            ||(O.getPoint(x-4,y+4)==0&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0)){
            result -= 500 ;
             
        } 
        if ((O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==0&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)==0)
            ||(O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==0)
            ||(O.getPoint(x-4,y-4)==0&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0)){
            result -= 500 ;
             
        } 

        //***************comp ***************8//
        //XOOOX 5 comp three

        if ((O.getPoint(x-1,y)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)==0)
            ||(O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==0)
            ||(O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==0)){
            result += 450 ;
             
        }
        if ((O.getPoint(x,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)==0)
                ||(O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==0)
                ||(O.getPoint(x,y-3)==0&&O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==0)){
                result += 450 ;
                 
        }
        if ((O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)==0)
                ||(O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==0)
                ||(O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==0)){
                result += 450 ;
                 
        }
        if ((O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)==0)
                ||(O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==0)
                ||(O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==0)){
                result += 450 ;
                 
        }
    
        //XOXOOX 6 comp broken three
        if ((O.getPoint(x-1,y)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)==1&&O.getPoint(x+4,y)==0)
            ||(O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==0)
            ||(O.getPoint(x-4,y)==0&&O.getPoint(x-3,y)==1&&O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==0)){
            result += 450 ;
             
        }
        if ((O.getPoint(x,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)==1&&O.getPoint(x,y+4)==0)
            ||(O.getPoint(x,y-3)==0&&O.getPoint(x,y+2)==1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==0)
            ||(O.getPoint(x,y-4)==0&&O.getPoint(x,y+3)==1&&O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==0)){
            result += 450 ;
             
        }
        if ((O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)==1&&O.getPoint(x+4,y-4)==0)
            ||(O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==0)
            ||(O.getPoint(x-4,y+4)==0&&O.getPoint(x-3,y+3)==1&&O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==0)){
            result += 450 ;
             
        }
        if ((O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)==1&&O.getPoint(x+4,y+4)==0)
            ||(O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==0)
            ||(O.getPoint(x-4,y-4)==0&&O.getPoint(x-3,y-3)==1&&O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==0)){
            result += 450 ;
             
        }
        
        //XOOXOX 6 comp broken three
        if ((O.getPoint(x-1,y)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==0&&O.getPoint(x+3,y)==1&&O.getPoint(x+4,y)==0)
            ||(O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)==0)
            ||(O.getPoint(x-4,y)==0&&O.getPoint(x-3,y)==1&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==0)){
            result += 450 ;
             
        }
        if ((O.getPoint(x,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==0&&O.getPoint(x,y+3)==1&&O.getPoint(x,y+4)==0)
            ||(O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)==0)
            ||(O.getPoint(x,y-4)==0&&O.getPoint(x,y-3)==1&&O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==0)){
            result += 450 ;
             
        } 
        if ((O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==0&&O.getPoint(x+3,y-3)==1&&O.getPoint(x+4,y-4)==0)
            ||(O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)==0)
            ||(O.getPoint(x-4,y+4)==0&&O.getPoint(x-3,y+3)==1&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==0)){
            result += 450 ;
             
        } 
        if ((O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==0&&O.getPoint(x+3,y+3)==1&&O.getPoint(x+4,y+4)==0)
            ||(O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)==0)
            ||(O.getPoint(x-4,y-4)==0&&O.getPoint(x-3,y-3)==1&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==0)){
            result += 450 ;
             
        } 
        
        return result;
        
    
    }

    //3 미만을 판단하는 함수(공수가 딱히 필요하지 않은 상황)
    public static int heuristicValue2(Node O, int num){ //heuristic value evaluate function
        num= game_num;
        int result = 0;

        int x = O.getPosition()[0];
        int y = O.getPosition()[1];
        

        //no threat condition//
        //XXOOX, comp
        if ((O.getPoint(x-2, y)==0&&O.getPoint(x-1, y)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y)==1&&O.getPoint(x+2, y)==0)
                ||(O.getPoint(x-3, y)==0&&O.getPoint(x-2, y)==0&&O.getPoint(x-1, y)==1&&O.getPoint(x, y)==1&&O.getPoint(x+1, y)==0)){
            result += 5;
        }
        if ((O.getPoint(x, y-2)==0&&O.getPoint(x, y-1)==0&&O.getPoint(x, y)==1&&O.getPoint(x, y+1)==1&&O.getPoint(x, y+2)==0)
                ||(O.getPoint(x, y-3)==0&&O.getPoint(x, y-2)==0&&O.getPoint(x, y-1)==1&&O.getPoint(x, y)==1&&O.getPoint(x, y+1)==0)){
            result += 5;
        }
        if ((O.getPoint(x-2, y+2)==0&&O.getPoint(x-1, y+1)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y-1)==1&&O.getPoint(x+2, y-2)==0)
                ||(O.getPoint(x-3, y+3)==0&&O.getPoint(x-2, y+2)==0&&O.getPoint(x-1, y+1)==1&&O.getPoint(x, y)==1&&O.getPoint(x+1, y-1)==0)){
            result += 5 ;
        }
        if ((O.getPoint(x-2, y-2)==0&&O.getPoint(x-1, y-1)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y+1)==1&&O.getPoint(x+2, y+2)==0)
                ||(O.getPoint(x-3, y-3)==0&&O.getPoint(x-2, y-2)==0&&O.getPoint(x-1, y-1)==1&&O.getPoint(x, y)==1&&O.getPoint(x+1, y+1)==0)){
            result += 5 ;
        }
        //XXOOX, pyr
        if ((O.getPoint(x-2, y)==0&&O.getPoint(x-1, y)==0&&O.getPoint(x, y)==-1&&O.getPoint(x+1, y)==-1&&O.getPoint(x+2, y)==0)
                ||(O.getPoint(x-3, y)==0&&O.getPoint(x-2, y)==0&&O.getPoint(x-1, y)==-1&&O.getPoint(x, y)==-1&&O.getPoint(x+1, y)==0)){
            result -= 5 ;
        }
        if ((O.getPoint(x, y-2)==0&&O.getPoint(x, y-1)==0&&O.getPoint(x, y)==-1&&O.getPoint(x, y+1)==-1&&O.getPoint(x, y+2)==0)
                ||(O.getPoint(x, y-3)==0&&O.getPoint(x, y-2)==0&&O.getPoint(x, y-1)==-1&&O.getPoint(x, y)==-1&&O.getPoint(x, y+1)==0)){
            result -= 5 ;
        }
        if ((O.getPoint(x-2, y+2)==0&&O.getPoint(x-1, y+1)==0&&O.getPoint(x, y)==-1&&O.getPoint(x+1, y-1)==-1&&O.getPoint(x+2, y-2)==0)
                ||(O.getPoint(x-3, y+3)==0&&O.getPoint(x-2, y+2)==0&&O.getPoint(x-1, y+1)==-1&&O.getPoint(x, y)==-1&&O.getPoint(x+1, y-1)==0)){
            result -= 5 ;
        }
        if ((O.getPoint(x-2, y-2)==0&&O.getPoint(x-1, y-1)==0&&O.getPoint(x, y)==-1&&O.getPoint(x+1, y+1)==-1&&O.getPoint(x+2, y+2)==0)
                ||(O.getPoint(x-3, y-3)==0&&O.getPoint(x-2, y-2)==0&&O.getPoint(x-1, y-1)==-1&&O.getPoint(x, y)==-1&&O.getPoint(x+1, y+1)==0)){
            result -= 5 ;
        }
        //XOOXX comp
        if ((O.getPoint(x-2, y)==0&&O.getPoint(x-1, y)==1&&O.getPoint(x, y)==1&&O.getPoint(x+1, y)==0&&O.getPoint(x+2, y)==0)
                ||(O.getPoint(x-1, y)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y)==1&&O.getPoint(x+2, y)==0&&O.getPoint(x+3, y)==0)){
            result += 5 ;
        }
        if ((O.getPoint(x, y-2)==0&&O.getPoint(x, y-1)==1&&O.getPoint(x, y)==1&&O.getPoint(x, y+1)==0&&O.getPoint(x, y+2)==0)
                ||(O.getPoint(x, y-1)==0&&O.getPoint(x, y)==1&&O.getPoint(x, y+1)==1&&O.getPoint(x, y+2)==0&&O.getPoint(x, y+3)==0)){
            result += 5 ;
        }
        if ((O.getPoint(x-2, y+2)==0&&O.getPoint(x-1, y+1)==1&&O.getPoint(x, y)==1&&O.getPoint(x+1, y-1)==0&&O.getPoint(x+2, y-2)==0)
                ||(O.getPoint(x-1, y+1)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y-1)==1&&O.getPoint(x+2, y-2)==0&&O.getPoint(x+3, y-3)==0)){
            result += 5 ;
        }
        if ((O.getPoint(x-2, y-2)==0&&O.getPoint(x-1, y-1)==1&&O.getPoint(x, y)==1&&O.getPoint(x+1, y+1)==0&&O.getPoint(x+2, y+2)==0)
                ||(O.getPoint(x-1, y-1)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y+1)==1&&O.getPoint(x+2, y+2)==0&&O.getPoint(x+3, y+3)==0)){
            result += 5 ;
        }
        //XOOXX pyr
        if ((O.getPoint(x-2, y)==0&&O.getPoint(x-1, y)==-1&&O.getPoint(x, y)==-1&&O.getPoint(x+1, y)==0&&O.getPoint(x+2, y)==0)
                ||(O.getPoint(x-1, y)==0&&O.getPoint(x, y)==-1&&O.getPoint(x+1, y)==-1&&O.getPoint(x+2, y)==0&&O.getPoint(x+3, y)==0)){
            result -= 5 ;
        }
        if ((O.getPoint(x, y-2)==0&&O.getPoint(x, y-1)==-1&&O.getPoint(x, y)==-1&&O.getPoint(x, y+1)==0&&O.getPoint(x, y+2)==0)
                ||(O.getPoint(x, y-1)==0&&O.getPoint(x, y)==-1&&O.getPoint(x, y+1)==-1&&O.getPoint(x, y+2)==0&&O.getPoint(x, y+3)==0)){
            result -= 5 ;
        }
        if ((O.getPoint(x-2, y+2)==0&&O.getPoint(x-1, y+1)==-1&&O.getPoint(x, y)==-1&&O.getPoint(x+1, y-1)==0&&O.getPoint(x+2, y-2)==0)
                ||(O.getPoint(x-1, y+1)==0&&O.getPoint(x, y)==-1&&O.getPoint(x+1, y-1)==-1&&O.getPoint(x+2, y-2)==0&&O.getPoint(x+3, y-3)==0)){
            result -= 5 ;
        }
        if ((O.getPoint(x-2, y-2)==0&&O.getPoint(x-1, y-1)==-1&&O.getPoint(x, y)==-1&&O.getPoint(x+1, y+1)==0&&O.getPoint(x+2, y+2)==0)
                ||(O.getPoint(x-1, y-1)==0&&O.getPoint(x, y)==-1&&O.getPoint(x+1, y+1)==-1&&O.getPoint(x+2, y+2)==0&&O.getPoint(x+3, y+3)==0)){
            result -= 5 ;
        }               
        //XOXOX comp
        if ((O.getPoint(x-3, y)==0&&O.getPoint(x-2, y)==1&&O.getPoint(x-1, y)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y)==0)
                ||(O.getPoint(x-1, y)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y)==0&&O.getPoint(x+2, y)==1&&O.getPoint(x+3, y)==0)){
            result += 5 ;
        }
        if ((O.getPoint(x, y-3)==0&&O.getPoint(x, y-2)==1&&O.getPoint(x, y-1)==0&&O.getPoint(x, y)==1&&O.getPoint(x, y+1)==0)
                ||(O.getPoint(x, y-1)==0&&O.getPoint(x, y)==1&&O.getPoint(x, y+1)==0&&O.getPoint(x, y+2)==1&&O.getPoint(x, y+3)==0)){
            result += 5 ;
        }
        if ((O.getPoint(x-3, y+3)==0&&O.getPoint(x-2, y+2)==1&&O.getPoint(x-1, y+1)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y-1)==0)
                ||(O.getPoint(x-1, y+1)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y-1)==0&&O.getPoint(x+2, y-2)==1&&O.getPoint(x+3, y-3)==0)){
            result += 5 ;
        }
        if ((O.getPoint(x-3, y-3)==0&&O.getPoint(x-2, y-2)==1&&O.getPoint(x-1, y-1)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y+1)==0)
                ||(O.getPoint(x-1, y-1)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y+1)==0&&O.getPoint(x+2, y+2)==1&&O.getPoint(x+3, y+3)==0)){
            result += 5 ;
        }
        //XOXOX pyr 
        if ((O.getPoint(x-3, y)==0&&O.getPoint(x-2, y)==1&&O.getPoint(x-1, y)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y)==0)
                ||(O.getPoint(x-1, y)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y)==0&&O.getPoint(x+2, y)==1&&O.getPoint(x+3, y)==0)){
            result -= 5 ;
        }
        if ((O.getPoint(x, y-3)==0&&O.getPoint(x, y-2)==1&&O.getPoint(x, y-1)==0&&O.getPoint(x, y)==1&&O.getPoint(x, y+1)==0)
                ||(O.getPoint(x, y-1)==0&&O.getPoint(x, y)==1&&O.getPoint(x, y+1)==0&&O.getPoint(x, y+2)==1&&O.getPoint(x, y+3)==0)){
            result -= 5 ;
        }
        if ((O.getPoint(x-3, y+3)==0&&O.getPoint(x-2, y+2)==1&&O.getPoint(x-1, y+1)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y-1)==0)
                ||(O.getPoint(x-1, y+1)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y-1)==0&&O.getPoint(x+2, y-2)==1&&O.getPoint(x+3, y-3)==0)){
            result -= 5 ;
        }
        if ((O.getPoint(x-3, y-3)==0&&O.getPoint(x-2, y-2)==1&&O.getPoint(x-1, y-1)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y+1)==0)
                ||(O.getPoint(x-1, y-1)==0&&O.getPoint(x, y)==1&&O.getPoint(x+1, y+1)==0&&O.getPoint(x+2, y+2)==1&&O.getPoint(x+3, y+3)==0)){
            result -= 5 ;
        }
        if( game_num<5 ){
        if (O.getPoint(7,7) == 1) result +=10; else if (O.getPoint(7, 7) == -1 ) result -=10;
        
        if (O.getPoint(6, 6) == 1) result +=4; else if (O.getPoint(6, 6) == -1 ) result -=4;
        if (O.getPoint(6, 7) == 1) result +=4; else if (O.getPoint(6, 7) == -1 ) result -=4;
        if (O.getPoint(6, 8) == 1) result +=4; else if (O.getPoint(6, 8) == -1 ) result -=4;
        if (O.getPoint(7, 6) == 1) result +=4; else if (O.getPoint(7, 6) == -1 ) result -=4;
        if (O.getPoint(7, 8) == 1) result +=4; else if (O.getPoint(7, 8) == -1 ) result -=4;
        if (O.getPoint(8, 6) == 1) result +=4; else if (O.getPoint(8, 6) == -1 ) result -=4;
        if (O.getPoint(8, 7) == 1) result +=4; else if (O.getPoint(8, 7) == -1 ) result -=4;
        if (O.getPoint(8, 8) == 1) result +=4; else if (O.getPoint(8, 8) == -1 ) result -=4;
        }
        if(5<game_num&& game_num<15){
        if (O.getPoint(5, 5) == 1) result +=3; else if (O.getPoint(5, 5) == -1 ) result -=3;
        if (O.getPoint(5, 6) == 1) result +=3; else if (O.getPoint(5, 6) == -1 ) result -=3;
        if (O.getPoint(5, 7) == 1) result +=3; else if (O.getPoint(5, 7) == -1 ) result -=3;
        if (O.getPoint(5, 8) == 1) result +=3; else if (O.getPoint(5, 8) == -1 ) result -=3;
        if (O.getPoint(5, 9) == 1) result +=3; else if (O.getPoint(5, 9) == -1 ) result -=3;

        if (O.getPoint(6, 5) == 1) result +=3; else if (O.getPoint(6, 5) == -1 ) result -=3;
        if (O.getPoint(6, 9) == 1) result +=3; else if (O.getPoint(6, 9) == -1 ) result -=3;
        if (O.getPoint(7, 5) == 1) result +=3; else if (O.getPoint(7, 5) == -1 ) result -=3;
        if (O.getPoint(7, 9) == 1) result +=3; else if (O.getPoint(7, 9) == -1 ) result -=3;
        if (O.getPoint(8, 5) == 1) result +=3; else if (O.getPoint(8, 5) == -1 ) result -=3;
        if (O.getPoint(8, 9) == 1) result +=3; else if (O.getPoint(8, 9) == -1 ) result -=3;
        
        if (O.getPoint(9, 5) == 1) result +=3; else if (O.getPoint(9, 5) == -1 ) result -=3;
        if (O.getPoint(9, 6) == 1) result +=3; else if (O.getPoint(9, 6) == -1 ) result -=3;
        if (O.getPoint(9, 7) == 1) result +=3; else if (O.getPoint(9, 7) == -1 ) result -=3;
        if (O.getPoint(9, 8) == 1) result +=3; else if (O.getPoint(9, 8) == -1 ) result -=3;
        if (O.getPoint(9, 9) == 1) result +=3; else if (O.getPoint(9, 9) == -1 ) result -=3;
        }
        if(15<game_num&&game_num<30){
        if (O.getPoint(10, 5) == 1) result +=3; else if (O.getPoint(10, 5) == -1 ) result -=3;
        if (O.getPoint(10, 6) == 1) result +=3; else if (O.getPoint(10, 6) == -1 ) result -=3;
        if (O.getPoint(10, 7) == 1) result +=3; else if (O.getPoint(10, 7) == -1 ) result -=3;
        if (O.getPoint(10, 8) == 1) result +=3; else if (O.getPoint(10, 8) == -1 ) result -=3;
        if (O.getPoint(10, 9) == 1) result +=3; else if (O.getPoint(10, 9) == -1 ) result -=3;
        if (O.getPoint(10, 10) == 1) result +=3; else if (O.getPoint(10, 10) == -1 ) result -=3;
        }
        game_over = 0;
        //수정
        result += (int) (Math.random() * 3) + 1;
        return result;
       // if (O.getPoint(x-1,y) == 1 && O.getPoint(x,y))
    }

    public static void move_pyr(Node O){ //사용자 입력 받아서 저장하기

        //scanner nextline으로 받기
//        System.out.println("돌을 둘 곳을 입력하세요");

        String message;
        Scanner scan = new Scanner(System.in);

        message = scan.nextLine();

        if (message.equals("START")){
            first_turn=true;
            return;         
        }

        //문자열 자르기
        char c1 = message.charAt(0);
        char c2 = message.charAt(1);
        String s2 = message.substring(1);
        
        int pyr_row = 0, pyr_col =0;

        //값 저장하기
        if (c1 == 'a'|| c1 == 'A') {
            pyr_col = 0;
        }
        else if (c1 == 'b' || c1 =='B') {
            pyr_col = 1;
        }
        else if (c1 == 'c' || c1 == 'C') {
            pyr_col = 2;
        }
        else if (c1 == 'd' || c1 == 'D') {
            pyr_col = 3;
        }
        else if (c1 == 'e' || c1 == 'E') {
            pyr_col = 4;
        }else if (c1 == 'f' || c1 == 'F') {pyr_col = 5;} 
        else if (c1 == 'g' || c1 == 'G') {pyr_col = 6;}
        else if (c1 == 'h' || c1 == 'H') {pyr_col = 7;}
        else if (c1 == 'i' || c1 == 'I') {pyr_col = 8;}
        else if (c1 == 'j' || c1 == 'J') {pyr_col = 9;}
        else if (c1 == 'k' || c1 == 'K') {pyr_col = 10;}
        else if (c1 == 'l' || c1 == 'L') {pyr_col = 11;}
        else if (c1 == 'm' || c1 == 'M') {pyr_col = 12;}
        else if (c1 == 'n' || c1 == 'N') {pyr_col = 13;}
        else if (c1 == 'o' || c1 == 'O') {pyr_col = 14;}
        if (s2.equals("1")){
            pyr_row = 14;
        }
        else if (s2.equals("2")){
            pyr_row = 13;
        }
        else if (c2 == '3'){
            pyr_row = 12;
        }
        else if (c2 == '4'){
            pyr_row = 11;
        }
        else if (c2 == '5'){pyr_row = 10;}
        else if (c2 == '6'){pyr_row = 9;}
        else if (c2 == '7'){pyr_row = 8;}
        else if (c2 == '8'){pyr_row = 7;}
        else if (c2 == '9'){pyr_row = 6;}
        else if (s2.equals("10")){pyr_row = 5;}
        else if (s2.equals("11")){pyr_row = 4;}
        else if (s2.equals("12")){pyr_row = 3;}
        else if (s2.equals("13")){pyr_row = 2;}
        else if (s2.equals("14")){pyr_row = 1;}
        else if (s2.equals("15")){pyr_row = 0;}
       // System.out.println(s2);
        //bestposition에 사용자가 둔 row, col 값을 저장한다.
        O.setPosition(pyr_row, pyr_col);
        O.setSearchSquare(pyr_row,pyr_col );
        
        
    }

    
    public static boolean renjurule(Node O,boolean pyr){ // 쌍삼 체크
        int num = 0;
    int x = O.getPosition()[0];
    int y = O.getPosition()[1];
    if (!pyr){
    //row 9개
    if ((O.getPoint(x-1,y,pyr) == 0 && O.getPoint(x,y,pyr)==1 && O.getPoint(x+1,y,pyr)==1 && O.getPoint(x+2,y,pyr)==1&&O.getPoint(x+3,y,pyr)==0)
            ||(O.getPoint(x-2,y,pyr) == 0 && O.getPoint(x-1,y,pyr)==1 && O.getPoint(x,y,pyr)==1 && O.getPoint(x+1,y,pyr)==1&&O.getPoint(x+2,y,pyr)==0)
            ||(O.getPoint(x-3,y,pyr) == 0 && O.getPoint(x-2,y,pyr)==1 && O.getPoint(x-1,y,pyr)==1 && O.getPoint(x,y,pyr)==1&&O.getPoint(x+1,y,pyr)==0)
            ||(O.getPoint(x-1,y,pyr) == 0 && O.getPoint(x,y,pyr)==1 && O.getPoint(x+1,y,pyr)==0 && O.getPoint(x+2,y,pyr)==1&&O.getPoint(x+3,y,pyr)==1&&O.getPoint(x+4, y,pyr)==0)
            ||(O.getPoint(x-3,y,pyr) == 0 && O.getPoint(x-2,y,pyr)==1 && O.getPoint(x-1,y,pyr)==0 && O.getPoint(x,y,pyr)==1&&O.getPoint(x+1,y,pyr)==1&&O.getPoint(x+2, y,pyr)==0)
            ||(O.getPoint(x-4,y,pyr) == 0 && O.getPoint(x-3,y,pyr)==1 && O.getPoint(x-2,y,pyr)==0 && O.getPoint(x-1,y,pyr)==1&&O.getPoint(x,y,pyr)==1&&O.getPoint(x+1, y,pyr)==0)
            ||(O.getPoint(x-1,y,pyr) == 0 && O.getPoint(x,y,pyr)==1 && O.getPoint(x+1,y,pyr)==1 && O.getPoint(x+2,y,pyr)==0&&O.getPoint(x+3,y,pyr)==1&&O.getPoint(x+4, y,pyr)==0)
            ||(O.getPoint(x-2,y,pyr) == 0 && O.getPoint(x-1,y,pyr)==1 && O.getPoint(x,y,pyr)==1 && O.getPoint(x+1,y,pyr)==0&&O.getPoint(x+2,y,pyr)==1&&O.getPoint(x+3, y,pyr)==0)
            ||(O.getPoint(x-4,y,pyr) == 0 && O.getPoint(x-3,y,pyr)==1 && O.getPoint(x-2,y,pyr)==1 && O.getPoint(x-1,y,pyr)==0&&O.getPoint(x,y,pyr)==1&&O.getPoint(x+1, y,pyr)==0)){
        num ++;
    }
    //column 12개
    if ((O.getPoint(x,y-1,pyr) == 0 && O.getPoint(x,y,pyr)==1 && O.getPoint(x,y+1,pyr)==1 && O.getPoint(x,y+2,pyr)==1&&O.getPoint(x,y+3,pyr)==0)
            ||(O.getPoint(x,y-2,pyr) == 0 && O.getPoint(x,y-1,pyr)==1 && O.getPoint(x,y,pyr)==1 && O.getPoint(x,y+1,pyr)==1&&O.getPoint(x,y+2,pyr)==0)
            ||(O.getPoint(x,y-3,pyr) == 0 && O.getPoint(x,y-2,pyr)==1 && O.getPoint(x,y-1,pyr)==1 && O.getPoint(x,y,pyr)==1&&O.getPoint(x,y+1,pyr)==0)
            ||(O.getPoint(x,y-1,pyr) == 0 && O.getPoint(x,y,pyr)==1 && O.getPoint(x,y+1,pyr)==0 && O.getPoint(x,y+2,pyr)==1&&O.getPoint(x,y+3,pyr)==1&&O.getPoint(x, y+4,pyr)==0)
            ||(O.getPoint(x,y-3,pyr) == 0 && O.getPoint(x,y-2,pyr)==1 && O.getPoint(x,y-1,pyr)==0 && O.getPoint(x,y,pyr)==1&&O.getPoint(x,y+1,pyr)==1&&O.getPoint(x, y+2,pyr)==0)
            ||(O.getPoint(x,y-4,pyr) == 0 && O.getPoint(x,y-3,pyr)==1 && O.getPoint(x,y-2,pyr)==0 && O.getPoint(x,y-1,pyr)==1&&O.getPoint(x,y,pyr)==1&&O.getPoint(x, y+1,pyr)==0)
            ||(O.getPoint(x,y-1,pyr) == 0 && O.getPoint(x,y,pyr)==1 && O.getPoint(x,y+1,pyr)==1 && O.getPoint(x,y+2,pyr)==0&&O.getPoint(x,y+3,pyr)==1&&O.getPoint(x, y+4,pyr)==0)
            ||(O.getPoint(x,y-2,pyr) == 0 && O.getPoint(x,y-1,pyr)==1 && O.getPoint(x,y,pyr)==1 && O.getPoint(x,y+1,pyr)==0&&O.getPoint(x,y+2,pyr)==1&&O.getPoint(x, y+3,pyr)==0)
            ||(O.getPoint(x,y-4,pyr) == 0 && O.getPoint(x,y-3,pyr)==1 && O.getPoint(x,y-2,pyr)==1 && O.getPoint(x,y-1,pyr)==0&&O.getPoint(x,y,pyr)==1&&O.getPoint(x, y+1,pyr)==0)){
        num ++;
    }
    
    //diagonal1
    if ((O.getPoint(x-1,y+1,pyr) == 0 && O.getPoint(x,y,pyr)==1 && O.getPoint(x+1,y-1,pyr)==1 && O.getPoint(x+2,y-2,pyr)==1&&O.getPoint(x+3,y-3,pyr)==0)
            ||(O.getPoint(x-2,y+2,pyr) == 0 && O.getPoint(x-1,y+1,pyr)==1 && O.getPoint(x,y,pyr)==1 && O.getPoint(x+1,y-1,pyr)==1&&O.getPoint(x+2,y-2,pyr)==0)
            ||(O.getPoint(x-3,y+3,pyr) == 0 && O.getPoint(x-2,y+2,pyr)==1 && O.getPoint(x-1,y+1,pyr)==1 && O.getPoint(x,y,pyr)==1&&O.getPoint(x+1,y-1,pyr)==0)
            ||(O.getPoint(x-1,y+1,pyr) == 0 && O.getPoint(x,y,pyr)==1 && O.getPoint(x+1,y-1,pyr)==0 && O.getPoint(x+2,y-2,pyr)==1&&O.getPoint(x+3,y-3,pyr)==1&&O.getPoint(x+4, y-4,pyr)==0)
            ||(O.getPoint(x-3,y+3,pyr) == 0 && O.getPoint(x-2,y+2,pyr)==1 && O.getPoint(x-1,y+1,pyr)==0 && O.getPoint(x,y,pyr)==1&&O.getPoint(x+1,y-1,pyr)==1&&O.getPoint(x+2, y-2,pyr)==0)
            ||(O.getPoint(x-4,y+4,pyr) == 0 && O.getPoint(x-3,y+3,pyr)==1 && O.getPoint(x-2,y+2,pyr)==0 && O.getPoint(x-1,y+1,pyr)==1&&O.getPoint(x,y,pyr)==1&&O.getPoint(x+1, y-1,pyr)==0)
            ||(O.getPoint(x-1,y+1,pyr) == 0 && O.getPoint(x,y,pyr)==1 && O.getPoint(x+1,y-1,pyr)==1 && O.getPoint(x+2,y-2,pyr)==0&&O.getPoint(x+3,y-3,pyr)==1&&O.getPoint(x+4, y-4,pyr)==0)
            ||(O.getPoint(x-2,y+2,pyr) == 0 && O.getPoint(x-1,y+1,pyr)==1 && O.getPoint(x,y,pyr)==1 && O.getPoint(x+1,y-1,pyr)==0&&O.getPoint(x+2,y-2,pyr)==1&&O.getPoint(x+3, y-3,pyr)==0)
            ||(O.getPoint(x-4,y+4,pyr) == 0 && O.getPoint(x-3,y+3,pyr)==1 && O.getPoint(x-2,y+2,pyr)==1 && O.getPoint(x-1,y+1,pyr)==0&&O.getPoint(x,y,pyr)==1&&O.getPoint(x+1, y-1,pyr)==0)){
        num ++;
    }
    
    //diagonal2
    if ((O.getPoint(x-1,y-1,pyr) == 0 && O.getPoint(x,y,pyr)==1 && O.getPoint(x+1,y+1,pyr)==1 && O.getPoint(x+2,y+2,pyr)==1&&O.getPoint(x+3,y+3,pyr)==0)
            ||(O.getPoint(x-2,y-2,pyr) == 0 && O.getPoint(x-1,y-1,pyr)==1 && O.getPoint(x,y,pyr)==1 && O.getPoint(x+1,y+1,pyr)==1&&O.getPoint(x+2,y+2,pyr)==0)
            ||(O.getPoint(x-3,y-3,pyr) == 0 && O.getPoint(x-2,y-2,pyr)==1 && O.getPoint(x-1,y-1,pyr)==1 && O.getPoint(x,y,pyr)==1&&O.getPoint(x+1,y+1,pyr)==0)
            ||(O.getPoint(x-1,y-1,pyr) == 0 && O.getPoint(x,y,pyr)==1 && O.getPoint(x+1,y+1,pyr)==0 && O.getPoint(x+2,y+2,pyr)==1&&O.getPoint(x+3,y+3,pyr)==1&&O.getPoint(x+4, y+4,pyr)==0)
            ||(O.getPoint(x-3,y-3,pyr) == 0 && O.getPoint(x-2,y-2,pyr)==1 && O.getPoint(x-1,y-1,pyr)==0 && O.getPoint(x,y,pyr)==1&&O.getPoint(x+1,y+1,pyr)==1&&O.getPoint(x+2, y+2,pyr)==0)
            ||(O.getPoint(x-4,y-4,pyr) == 0 && O.getPoint(x-3,y-3,pyr)==1 && O.getPoint(x-2,y-2,pyr)==0 && O.getPoint(x-1,y-1,pyr)==1&&O.getPoint(x,y,pyr)==1&&O.getPoint(x+1, y+1,pyr)==0)
            ||(O.getPoint(x-1,y-1,pyr) == 0 && O.getPoint(x,y,pyr)==1 && O.getPoint(x+1,y+1,pyr)==1 && O.getPoint(x+2,y+2,pyr)==0&&O.getPoint(x+3,y+3,pyr)==1&&O.getPoint(x+4, y+4,pyr)==0)
            ||(O.getPoint(x-2,y-2,pyr) == 0 && O.getPoint(x-1,y-1,pyr)==1 && O.getPoint(x,y,pyr)==1 && O.getPoint(x+1,y+1,pyr)==0&&O.getPoint(x+2,y+2,pyr)==1&&O.getPoint(x+3, y+3,pyr)==0)
            ||(O.getPoint(x-4,y-4,pyr) == 0 && O.getPoint(x-3,y-3,pyr)==1 && O.getPoint(x-2,y-2,pyr)==1 && O.getPoint(x-1,y-1,pyr)==0&&O.getPoint(x,y,pyr)==1&&O.getPoint(x+1, y+1,pyr)==0)){
        num ++;
    }
    } else {
        //row 9개
        if ((O.getPoint(x-1,y,pyr) == 0 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x+1,y,pyr)==-1 && O.getPoint(x+2,y,pyr)==-1&&O.getPoint(x+3,y,pyr)==0)
                ||(O.getPoint(x-2,y,pyr) == 0 && O.getPoint(x-1,y,pyr)==-1 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x+1,y,pyr)==-1&&O.getPoint(x+2,y,pyr)==0)
                ||(O.getPoint(x-3,y,pyr) == 0 && O.getPoint(x-2,y,pyr)==-1 && O.getPoint(x-1,y,pyr)==-1 && O.getPoint(x,y,pyr)==-1&&O.getPoint(x+1,y,pyr)==0)
                ||(O.getPoint(x-1,y,pyr) == 0 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x+1,y,pyr)==0 && O.getPoint(x+2,y,pyr)==-1&&O.getPoint(x+3,y,pyr)==-1&&O.getPoint(x+4, y,pyr)==0)
                ||(O.getPoint(x-3,y,pyr) == 0 && O.getPoint(x-2,y,pyr)==-1 && O.getPoint(x-1,y,pyr)==0 && O.getPoint(x,y,pyr)==-1&&O.getPoint(x+1,y,pyr)==-1&&O.getPoint(x+2, y,pyr)==0)
                ||(O.getPoint(x-4,y,pyr) == 0 && O.getPoint(x-3,y,pyr)==-1 && O.getPoint(x-2,y,pyr)==0 && O.getPoint(x-1,y,pyr)==-1&&O.getPoint(x,y,pyr)==-1&&O.getPoint(x+1, y,pyr)==0)
                ||(O.getPoint(x-1,y,pyr) == 0 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x+1,y,pyr)==-1 && O.getPoint(x+2,y,pyr)==0&&O.getPoint(x+3,y,pyr)==-1&&O.getPoint(x+4, y,pyr)==0)
                ||(O.getPoint(x-2,y,pyr) == 0 && O.getPoint(x-1,y,pyr)==-1 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x+1,y,pyr)==0&&O.getPoint(x+2,y,pyr)==-1&&O.getPoint(x+3, y,pyr)==0)
                ||(O.getPoint(x-4,y,pyr) == 0 && O.getPoint(x-3,y,pyr)==-1 && O.getPoint(x-2,y,pyr)==-1 && O.getPoint(x-1,y,pyr)==0&&O.getPoint(x,y,pyr)==-1&&O.getPoint(x+1, y,pyr)==0)){
            num ++;
        }
        //column 12개
        if ((O.getPoint(x,y-1,pyr) == 0 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x,y+1,pyr)==-1 && O.getPoint(x,y+2,pyr)==-1&&O.getPoint(x,y+3,pyr)==0)
                ||(O.getPoint(x,y-2,pyr) == 0 && O.getPoint(x,y-1,pyr)==-1 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x,y+1,pyr)==-1&&O.getPoint(x,y+2,pyr)==0)
                ||(O.getPoint(x,y-3,pyr) == 0 && O.getPoint(x,y-2,pyr)==-1 && O.getPoint(x,y-1,pyr)==-1 && O.getPoint(x,y,pyr)==-1&&O.getPoint(x,y+1,pyr)==0)
                ||(O.getPoint(x,y-1,pyr) == 0 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x,y+1,pyr)==0 && O.getPoint(x,y+2,pyr)==-1&&O.getPoint(x,y+3,pyr)==-1&&O.getPoint(x, y+4,pyr)==0)
                ||(O.getPoint(x,y-3,pyr) == 0 && O.getPoint(x,y-2,pyr)==-1 && O.getPoint(x,y-1,pyr)==0 && O.getPoint(x,y,pyr)==-1&&O.getPoint(x,y+1,pyr)==-1&&O.getPoint(x, y+2,pyr)==0)
                ||(O.getPoint(x,y-4,pyr) == 0 && O.getPoint(x,y-3,pyr)==-1 && O.getPoint(x,y-2,pyr)==0 && O.getPoint(x,y-1,pyr)==-1&&O.getPoint(x,y,pyr)==-1&&O.getPoint(x, y+1,pyr)==0)
                ||(O.getPoint(x,y-1,pyr) == 0 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x,y+1,pyr)==-1 && O.getPoint(x,y+2,pyr)==0&&O.getPoint(x,y+3,pyr)==-1&&O.getPoint(x, y+4,pyr)==0)
                ||(O.getPoint(x,y-2,pyr) == 0 && O.getPoint(x,y-1,pyr)==-1 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x,y+1,pyr)==0&&O.getPoint(x,y+2,pyr)==-1&&O.getPoint(x, y+3,pyr)==0)
                ||(O.getPoint(x,y-4,pyr) == 0 && O.getPoint(x,y-3,pyr)==-1 && O.getPoint(x,y-2,pyr)==-1 && O.getPoint(x,y-1,pyr)==0&&O.getPoint(x,y,pyr)==-1&&O.getPoint(x, y+1,pyr)==0)){
            num ++;
        }
        
        //diagonal1
        if ((O.getPoint(x-1,y+1,pyr) == 0 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x+1,y-1,pyr)==-1 && O.getPoint(x+2,y-2,pyr)==-1&&O.getPoint(x+3,y-3,pyr)==0)
                ||(O.getPoint(x-2,y+2,pyr) == 0 && O.getPoint(x-1,y+1,pyr)==-1 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x+1,y-1,pyr)==-1&&O.getPoint(x+2,y-2,pyr)==0)
                ||(O.getPoint(x-3,y+3,pyr) == 0 && O.getPoint(x-2,y+2,pyr)==-1 && O.getPoint(x-1,y+1,pyr)==-1 && O.getPoint(x,y,pyr)==-1&&O.getPoint(x+1,y-1,pyr)==0)
                ||(O.getPoint(x-1,y+1,pyr) == 0 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x+1,y-1,pyr)==0 && O.getPoint(x+2,y-2,pyr)==-1&&O.getPoint(x+3,y-3,pyr)==-1&&O.getPoint(x+4, y-4,pyr)==0)
                ||(O.getPoint(x-3,y+3,pyr) == 0 && O.getPoint(x-2,y+2,pyr)==-1 && O.getPoint(x-1,y+1,pyr)==0 && O.getPoint(x,y,pyr)==-1&&O.getPoint(x+1,y-1,pyr)==-1&&O.getPoint(x+2, y-2,pyr)==0)
                ||(O.getPoint(x-4,y+4,pyr) == 0 && O.getPoint(x-3,y+3,pyr)==-1 && O.getPoint(x-2,y+2,pyr)==0 && O.getPoint(x-1,y+1,pyr)==-1&&O.getPoint(x,y,pyr)==-1&&O.getPoint(x+1, y-1,pyr)==0)
                ||(O.getPoint(x-1,y+1,pyr) == 0 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x+1,y-1,pyr)==-1 && O.getPoint(x+2,y-2,pyr)==0&&O.getPoint(x+3,y-3,pyr)==-1&&O.getPoint(x+4, y-4,pyr)==0)
                ||(O.getPoint(x-2,y+2,pyr) == 0 && O.getPoint(x-1,y+1,pyr)==-1 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x+1,y-1,pyr)==0&&O.getPoint(x+2,y-2,pyr)==-1&&O.getPoint(x+3, y-3,pyr)==0)
                ||(O.getPoint(x-4,y+4,pyr) == 0 && O.getPoint(x-3,y+3,pyr)==-1 && O.getPoint(x-2,y+2,pyr)==-1 && O.getPoint(x-1,y+1,pyr)==0&&O.getPoint(x,y,pyr)==-1&&O.getPoint(x+1, y-1,pyr)==0)){
            num ++;
        }
        
        //diagonal2
        if ((O.getPoint(x-1,y-1,pyr) == 0 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x+1,y+1,pyr)==-1 && O.getPoint(x+2,y+2,pyr)==-1&&O.getPoint(x+3,y+3,pyr)==0)
                ||(O.getPoint(x-2,y-2,pyr) == 0 && O.getPoint(x-1,y-1,pyr)==-1 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x+1,y+1,pyr)==-1&&O.getPoint(x+2,y+2,pyr)==0)
                ||(O.getPoint(x-3,y-3,pyr) == 0 && O.getPoint(x-2,y-2,pyr)==-1 && O.getPoint(x-1,y-1,pyr)==-1 && O.getPoint(x,y,pyr)==-1&&O.getPoint(x+1,y+1,pyr)==0)
                ||(O.getPoint(x-1,y-1,pyr) == 0 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x+1,y+1,pyr)==0 && O.getPoint(x+2,y+2,pyr)==-1&&O.getPoint(x+3,y+3,pyr)==-1&&O.getPoint(x+4, y+4,pyr)==0)
                ||(O.getPoint(x-3,y-3,pyr) == 0 && O.getPoint(x-2,y-2,pyr)==-1 && O.getPoint(x-1,y-1,pyr)==0 && O.getPoint(x,y,pyr)==-1&&O.getPoint(x+1,y+1,pyr)==-1&&O.getPoint(x+2, y+2,pyr)==0)
                ||(O.getPoint(x-4,y-4,pyr) == 0 && O.getPoint(x-3,y-3,pyr)==-1 && O.getPoint(x-2,y-2,pyr)==0 && O.getPoint(x-1,y-1,pyr)==-1&&O.getPoint(x,y,pyr)==-1&&O.getPoint(x+1, y+1,pyr)==0)
                ||(O.getPoint(x-1,y-1,pyr) == 0 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x+1,y+1,pyr)==-1 && O.getPoint(x+2,y+2,pyr)==0&&O.getPoint(x+3,y+3,pyr)==-1&&O.getPoint(x+4, y+4,pyr)==0)
                ||(O.getPoint(x-2,y-2,pyr) == 0 && O.getPoint(x-1,y-1,pyr)==-1 && O.getPoint(x,y,pyr)==-1 && O.getPoint(x+1,y+1,pyr)==0&&O.getPoint(x+2,y+2,pyr)==-1&&O.getPoint(x+3, y+3,pyr)==0)
                ||(O.getPoint(x-4,y-4,pyr) == 0 && O.getPoint(x-3,y-3,pyr)==-1 && O.getPoint(x-2,y-2,pyr)==-1 && O.getPoint(x-1,y-1,pyr)==0&&O.getPoint(x,y,pyr)==-1&&O.getPoint(x+1, y+1,pyr)==0)){
            num ++;
        }
    }
    if( num>=2){
        return true;
    }else{
        return false;
    }
}

    //threat을 판단하고  costsquare를 업데이트한다.(cmp)
    public static int threatCheck(Node O, boolean pyr){
        int num = 0;
        boolean[] extrabit = {false,false,false,false};
        int x = O.getPosition()[0];
        int y = O.getPosition()[1];
        
        if (//row
                (O.getPoint(x-1,y)!=1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)==1&&O.getPoint(x+4,y)==1&&O.getPoint(x+5,y)!=1)
                ||(O.getPoint(x-2,y)!=1&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)==1&&O.getPoint(x+4,y)!=1)
                ||(O.getPoint(x-3,y)!=1&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)!=1)
                ||(O.getPoint(x-4,y)!=1&&O.getPoint(x-3,y)==1&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)!=1)
                ||(O.getPoint(x-5,y)!=1&&O.getPoint(x-4,y)==1&&O.getPoint(x-3,y)==1&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)!=1)
    //col
                ||(O.getPoint(x,y-1)!=1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)==1&&O.getPoint(x,y+4)==1&&O.getPoint(x,y+5)!=1)
                ||(O.getPoint(x,y-2)!=1&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)==1&&O.getPoint(x,y+4)!=1)
                ||(O.getPoint(x,y-3)!=1&&O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)!=1)
                ||(O.getPoint(x,y-4)!=1&&O.getPoint(x,y-3)==1&&O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)!=1)
                ||(O.getPoint(x,y-5)!=1&&O.getPoint(x,y-4)==1&&O.getPoint(x,y-3)==1&&O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)!=1)
    //diag
                ||(O.getPoint(x-1,y+1)!=1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)==1&&O.getPoint(x+4,y-4)==1&&O.getPoint(x+5,y-5)!=1)
                ||(O.getPoint(x-2,y+2)!=1&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)==1&&O.getPoint(x+4,y-4)!=1)
                ||(O.getPoint(x-3,y+3)!=1&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)!=1)
                ||(O.getPoint(x-4,y+4)!=1&&O.getPoint(x-3,y+3)==1&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)!=1)
                ||(O.getPoint(x-5,y+5)!=1&&O.getPoint(x-4,y+4)==1&&O.getPoint(x-3,y+3)==1&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)!=1)
    //diag      
                ||(O.getPoint(x-1,y-1)!=1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)==1&&O.getPoint(x+4,y+4)==1&&O.getPoint(x+5,y+5)!=1)
                ||(O.getPoint(x-2,y-2)!=1&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)==1&&O.getPoint(x+4,y+4)!=1)
                ||(O.getPoint(x-3,y-3)!=1&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)!=1)
                ||(O.getPoint(x-4,y-4)!=1&&O.getPoint(x-3,y-3)==1&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)!=1)
                ||(O.getPoint(x-5,y-5)!=1&&O.getPoint(x-4,y-4)==1&&O.getPoint(x-3,y-3)==1&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)!=1)

                ){

            return 3;
        }
        
        // XOOOOX unblocked four comp
        if (O.getPoint(x-1,y)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)==1&&O.getPoint(x+4,y)==0)
        {MyObject tempArr = new MyObject(x-1,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+4,y);
        O.costSquares.add(tempArr2);
        return 3;}
        else if (O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)==0)
        {MyObject tempArr = new MyObject(x-2,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+3,y);
        O.costSquares.add(tempArr2);
        return 3;}
        else if(O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==0)
        {MyObject tempArr = new MyObject(x-3,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+2,y);
        O.costSquares.add(tempArr2);
        return 3;}
        else if (O.getPoint(x-4,y)==0&&O.getPoint(x-3,y)==1&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==0){
            MyObject tempArr = new MyObject(x-4,y);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+1,y);
            O.costSquares.add(tempArr2);
            return 3;
            }
        
        if (O.getPoint(x,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)==1&&O.getPoint(x,y+4)==0)
        {MyObject tempArr = new MyObject(x,y-1);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x,y+4);
        O.costSquares.add(tempArr2);
        return 3;}
        else if (O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)==0)
        {MyObject tempArr = new MyObject(x,y-2);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x,y+3);
        O.costSquares.add(tempArr2);
        return 3;}
        else if(O.getPoint(x,y-3)==0&&O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==0)
        {MyObject tempArr = new MyObject(x,y-3);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x,y+2);
        O.costSquares.add(tempArr2);
        return 3;}
        else if (O.getPoint(x,y-4)==0&&O.getPoint(x,y-3)==1&&O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==0){
            MyObject tempArr = new MyObject(x,y-4);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x,y+1);
            O.costSquares.add(tempArr2);
            return 3;}
        
        
            if (O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)==1&&O.getPoint(x+4,y-4)==0)
                    {MyObject tempArr = new MyObject(x-1,y+1);
                    O.costSquares.add(tempArr);
                    MyObject tempArr2 = new MyObject(x+4,y-4);
                    O.costSquares.add(tempArr2);
                    return 3;}
            else if (O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)==0)
            {MyObject tempArr = new MyObject(x-2,y+2);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+3,y-3);
            O.costSquares.add(tempArr2);
            return 3;}
            else if(O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==0)
            {MyObject tempArr = new MyObject(x-3,y+3);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+2,y-2);
            O.costSquares.add(tempArr2);
            return 3;}
            else if (O.getPoint(x-4,y+4)==0&&O.getPoint(x-3,y+3)==1&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==0){
                MyObject tempArr = new MyObject(x-4,y+4);
                O.costSquares.add(tempArr);
                MyObject tempArr2 = new MyObject(x+1,y-1);
                O.costSquares.add(tempArr2);
                return 3;}
            
            if (O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)==1&&O.getPoint(x+4,y+4)==0)
            {MyObject tempArr = new MyObject(x-1,y-1);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+4,y+4);
            O.costSquares.add(tempArr2);
            return 3;}
            else if (O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)==0)
            {MyObject tempArr = new MyObject(x-2,y-2);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+3,y+3);
            O.costSquares.add(tempArr2);
            return 3;}
            else if (O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==0)
            {MyObject tempArr = new MyObject(x-3,y-3);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+2,y+2);
            O.costSquares.add(tempArr2);
            return 3;}
            else if (O.getPoint(x-4,y-4)==0&&O.getPoint(x-3,y-3)==1&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==0){
                MyObject tempArr = new MyObject(x-4,y-4);
                O.costSquares.add(tempArr);
                MyObject tempArr2 = new MyObject(x+1,y+1);
                O.costSquares.add(tempArr2);
                return 3;}

        //OOOXO comp, 1,1,1,0,1 
        
        if (O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)==0&&O.getPoint(x+4,y)==1)
        {MyObject tempArr = new MyObject(x+3,y);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x-1, y)==0) extrabit[0] = true;}
        
        else if (O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==0&&O.getPoint(x+3,y)==1)
        {MyObject tempArr = new MyObject(x+2,y);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x-2, y)==0) extrabit[0] = true;}
        
        else if(O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==1){
        MyObject tempArr = new MyObject(x+1,y);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x-3, y)==0) extrabit[0] = true;}
        
        else if(O.getPoint(x-4,y)==1&&O.getPoint(x-3,y)==1&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==1){
            MyObject tempArr = new MyObject(x-1,y);
            O.costSquares.add(tempArr);
            num++;
            if(O.getPoint(x-5, y)==0) extrabit[0] = true;}
        
        if (O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)==0&&O.getPoint(x,y+4)==1)
        {MyObject tempArr = new MyObject(x,y+3);
        O.costSquares.add(tempArr);
        num++;if(O.getPoint(x, y-1)==0) extrabit[1] = true;}
        else if (O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==0&&O.getPoint(x,y+3)==1)
        {MyObject tempArr = new MyObject(x,y+2);
        O.costSquares.add(tempArr);
        num++;if(O.getPoint(x, y-2)==0) extrabit[1] = true;}
        else if(O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==1){
        MyObject tempArr = new MyObject(x,y+1);
        O.costSquares.add(tempArr);
        num++;if(O.getPoint(x, y-3)==0) extrabit[1] = true;}
        else if(O.getPoint(x,y-4)==1&&O.getPoint(x,y-3)==1&&O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==1){
            MyObject tempArr = new MyObject(x,y-1);
            O.costSquares.add(tempArr);
            num++;if(O.getPoint(x, y-5)==0) extrabit[1] = true;}
        
        if (O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)==0&&O.getPoint(x+4,y-4)==1)
        {MyObject tempArr = new MyObject(x+3,y-3);
        O.costSquares.add(tempArr);
        num++;if(O.getPoint(x-1, y+1)==0) extrabit[2] = true;}
        else if (O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==0&&O.getPoint(x+3,y-3)==1)
        {MyObject tempArr = new MyObject(x+2,y-2);
        O.costSquares.add(tempArr);
        num++;if(O.getPoint(x-2, y+2)==0) extrabit[2] = true;}
        else if(O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==1){
        MyObject tempArr = new MyObject(x+1,y-1);
        O.costSquares.add(tempArr);
        num++;if(O.getPoint(x-3, y+3)==0) extrabit[2] = true;}
        else if(O.getPoint(x-4,y+4)==1&&O.getPoint(x-3,y+3)==1&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==1){
            MyObject tempArr = new MyObject(x-1,y+1);
            O.costSquares.add(tempArr);
            num++;if(O.getPoint(x-5, y+5)==0) extrabit[2] = true;}
        
        if (O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)==0&&O.getPoint(x+4,y+4)==1)
        {MyObject tempArr = new MyObject(x+3,y+3);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x-1, y-1)==0) extrabit[3] = true;}
        else if (O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==0&&O.getPoint(x+3,y+3)==1)
        {MyObject tempArr = new MyObject(x+2,y+2);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x-2, y-2)==0) extrabit[3] = true;}
        else if(O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==1){
        MyObject tempArr = new MyObject(x+1,y+1);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x-3, y-3)==0) extrabit[3] = true;}
        else if(O.getPoint(x-4,y-4)==1&&O.getPoint(x-3,y-3)==1&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==1){
            MyObject tempArr = new MyObject(x-1,y-1);
            O.costSquares.add(tempArr);
            num++;
            if(O.getPoint(x-5, y-5)==0) extrabit[3] = true;}
    
        //.OOXOO.   comp
        if(extrabit[0]=false){
        if (O.getPoint(x-1, y)!=1&&O.getPoint(x+5, y)!=1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==0&&O.getPoint(x+3,y)==1&&O.getPoint(x+4,y)==1)
        {
            MyObject tempArr = new MyObject(x+2,y);
        O.costSquares.add(tempArr);
        num++;
        }
        else if (O.getPoint(x-2, y)!=1&&O.getPoint(x+4, y)!=1&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)==1)
        {MyObject tempArr = new MyObject(x+1,y);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-4, y)!=1&&O.getPoint(x+2, y)!=1&&O.getPoint(x-3,y)==1&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1){
        MyObject tempArr = new MyObject(x-1,y);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-5, y)!=1&&O.getPoint(x+1, y)!=1&&O.getPoint(x-4,y)==1&&O.getPoint(x-3,y)==1&&O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1){
            MyObject tempArr = new MyObject(x-2,y);
            O.costSquares.add(tempArr);
            num++;}
        }
        if(extrabit[1]=false){
        if (O.getPoint(x, y-1)!=1&&O.getPoint(x, y+5)!=1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==0&&O.getPoint(x,y+3)==1&&O.getPoint(x,y+4)==1)
        {MyObject tempArr = new MyObject(x,y+2);
        O.costSquares.add(tempArr);
        num++;}
        else if (O.getPoint(x, y-2)!=1&&O.getPoint(x, y+4)!=1&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)==1)
        {MyObject tempArr = new MyObject(x,y+1);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x, y-4)!=1&&O.getPoint(x, y+2)!=1&&O.getPoint(x,y-3)==1&&O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1){
        MyObject tempArr = new MyObject(x,y-1);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x, y-5)!=1&&O.getPoint(x, y+1)!=1&&O.getPoint(x,y-4)==1&&O.getPoint(x,y-3)==1&&O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1){
            MyObject tempArr = new MyObject(x,y-2);
            O.costSquares.add(tempArr);
            num++;}
        }
        if(extrabit[2]=false){
        if (O.getPoint(x-1, y+1)!=1&&O.getPoint(x+5, y-5)!=1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==0&&O.getPoint(x+3,y-3)==1&&O.getPoint(x+4,y-4)==1)
        {MyObject tempArr = new MyObject(x+2,y-2);
        O.costSquares.add(tempArr);
        num++;}
        else if (O.getPoint(x-2, y+2)!=1&&O.getPoint(x+4, y-4)!=1&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)==1)
        {MyObject tempArr = new MyObject(x+1,y-1);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-4, y+4)!=1&&O.getPoint(x+2, y-2)!=1&&O.getPoint(x-3,y+3)==1&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1){
        MyObject tempArr = new MyObject(x-1,y+1);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-5, y+5)!=1&&O.getPoint(x+1, y-1)!=1&&O.getPoint(x-4,y+4)==1&&O.getPoint(x-3,y+3)==1&&O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1){
            MyObject tempArr = new MyObject(x-2,y+2);
            O.costSquares.add(tempArr);
            num++;}
        }
        if(extrabit[3]=false){
        if (O.getPoint(x-1, y-1)!=1&&O.getPoint(x+5, y+5)!=1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==0&&O.getPoint(x+3,y+3)==1&&O.getPoint(x+4,y+4)==1)
        {MyObject tempArr = new MyObject(x+2,y+2);
        O.costSquares.add(tempArr);
        num++;}
        else if (O.getPoint(x-2, y-2)!=1&&O.getPoint(x+4, y+4)!=1&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)==1)
        {MyObject tempArr = new MyObject(x+1,y+1);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-4, y-4)!=1&&O.getPoint(x+2, y+2)!=1&&O.getPoint(x-3,y-3)==1&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1){
        MyObject tempArr = new MyObject(x-1,y-1);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-5, y-5)!=1&&O.getPoint(x+1, y+1)!=1&&O.getPoint(x-4,y-4)==1&&O.getPoint(x-3,y-3)==1&&O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1){
            MyObject tempArr = new MyObject(x-2,y-2);
            O.costSquares.add(tempArr);
            num++;}
        }
//OXOOO comp, 1,0,1,1,1 
                
        if(extrabit[0]=false){
        if (O.getPoint(x,y)==1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)==1&&O.getPoint(x+4,y)==1)
        {MyObject tempArr = new MyObject(x+1,y);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+5, y)==0) extrabit[0] = true;}
                
        else if(O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==1){
        MyObject tempArr = new MyObject(x-1,y);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+3, y)==0) extrabit[0] = true;}
        
        else if (O.getPoint(x-3,y)==1&&O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1)
        {MyObject tempArr = new MyObject(x-2,y);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+2, y)==0) extrabit[0] = true;}

        else if(O.getPoint(x-4,y)==1&&O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1){
            MyObject tempArr = new MyObject(x-3,y);
            O.costSquares.add(tempArr);
            num++;
            if(O.getPoint(x+1, y)==0) extrabit[0] = true;}
        }
        
        if(extrabit[1]=false){
        
        if (O.getPoint(x,y)==1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)==1&&O.getPoint(x,y+4)==1)
        {MyObject tempArr = new MyObject(x,y+1);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x, y+5)==0) extrabit[1] = true;}
                
        else if(O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==1){
        MyObject tempArr = new MyObject(x,y-1);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x, y+3)==0) extrabit[1] = true;}
        
        else if (O.getPoint(x,y-3)==1&&O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1)
        {MyObject tempArr = new MyObject(x,y-2);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x, y+2)==0) extrabit[1] = true;}

        else if(O.getPoint(x,y-4)==1&&O.getPoint(x,y-3)==0&&O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1){
            MyObject tempArr = new MyObject(x,y-3);
            O.costSquares.add(tempArr);
            num++;if(O.getPoint(x, y+1)==0) extrabit[1] = true;}
        }
        if(extrabit[2]=false){
        if (O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)==1&&O.getPoint(x+4,y-4)==1)
        {MyObject tempArr = new MyObject(x+1,y-1);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+5, y-5)==0) extrabit[2] = true;}
                
        else if(O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-2)==1&&O.getPoint(x+2,y-2)==1){
        MyObject tempArr = new MyObject(x-1,y+1);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+3, y-3)==0) extrabit[2] = true;}
        
        else if (O.getPoint(x-3,y+3)==1&&O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1)
        {MyObject tempArr = new MyObject(x-2,y+2);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+2, y-2)==0) extrabit[2] = true;}

        else if(O.getPoint(x-4,y+4)==1&&O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1){
            MyObject tempArr = new MyObject(x-3,y+3);
            O.costSquares.add(tempArr);
            num++;
            if(O.getPoint(x+1, y-1)==0) extrabit[2] = true;}
        
        }
        if(extrabit[3]=false){
        if (O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)==1&&O.getPoint(x+4,y+4)==1)
        {MyObject tempArr = new MyObject(x+1,y+1);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+5, y+5)==0) extrabit[3] = true;}
                
        else if(O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==1){
        MyObject tempArr = new MyObject(x-1,y-1);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+3, y+3)==0) extrabit[3] = true;}
        
        else if (O.getPoint(x-3,y-3)==1&&O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1)
        {MyObject tempArr = new MyObject(x-2,y-2);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+2, y+2)==0) extrabit[3] = true;}

        else if(O.getPoint(x-4,y-4)==1&&O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1){
            MyObject tempArr = new MyObject(x-3,y-3);
            O.costSquares.add(tempArr);
            num++;
            if(O.getPoint(x+1, y+1)==0) extrabit[3] = true;}
        }
        
        //●OOOOX 6 comp blocked four
        
        if ((O.getPoint(x-1,y,false)==-1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)==1&&O.getPoint(x+4,y)==0))
        {
            MyObject tempArr = new MyObject(x+4,y);
            O.costSquares.add(tempArr);         
            num++;
        }else if (O.getPoint(x-2,y,false)==-1&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)==0){
            MyObject tempArr = new MyObject(x+3,y);
            O.costSquares.add(tempArr);
            num++;
        }else if(O.getPoint(x-3,y,false)==-1&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==0)
        {
            MyObject tempArr = new MyObject(x+2,y);
            O.costSquares.add(tempArr);
            num++;
        }
        else if (O.getPoint(x-4,y,false)==-1&&O.getPoint(x-3,y)==1&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==0)
        {
            MyObject tempArr = new MyObject(x+1,y);
            O.costSquares.add(tempArr);
            num++;
        }
        
        
        if ((O.getPoint(x,y-1,false)==-1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)==1&&O.getPoint(x,y+4)==0)
            ){
            MyObject tempArr = new MyObject(x,y+4);
            O.costSquares.add(tempArr);
            num++;
        }else if(O.getPoint(x,y-2,false)==-1&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)==0)
        {
            MyObject tempArr = new MyObject(x,y+3);
            O.costSquares.add(tempArr);
            num++;
        }
        else if(O.getPoint(x,y-3,false)==-1&&O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==0)
        {MyObject tempArr = new MyObject(x,y+2);
        O.costSquares.add(tempArr);
        num++;
        }
        else if(O.getPoint(x,y-4,false)==-1&&O.getPoint(x,y-3)==1&&O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==0)
        {
            MyObject tempArr = new MyObject(x,y+1);
            O.costSquares.add(tempArr);
            num++;              
        }

        
        if (O.getPoint(x-1,y+1,false)==-1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)==1&&O.getPoint(x+4,y-4)==0)
                {MyObject tempArr = new MyObject(x+4,y-4);
                O.costSquares.add(tempArr);
                num++;}
        else if 
                (O.getPoint(x-2,y+2,false)==-1&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)==0)
        {
            MyObject tempArr = new MyObject(x+3,y-3);
            O.costSquares.add(tempArr);
            num++;
        }
            else if (O.getPoint(x-3,y+3,false)==-1&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==0)
            {MyObject tempArr = new MyObject(x+2,y-2);
            O.costSquares.add(tempArr);
            num++;}
        
                else if (O.getPoint(x-4,y+4,false)==-1&&O.getPoint(x-3,y+3)==1&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==0)
            {MyObject tempArr = new MyObject(x+1,y-1);
            O.costSquares.add(tempArr);
            num++;}
        
                
        if (O.getPoint(x-1,y-1,false)==-1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)==1&&O.getPoint(x+4,y+4)==0)
        {MyObject tempArr = new MyObject(x+4,y+4);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-2,y-2,false)==-1&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)==0)
        {MyObject tempArr = new MyObject(x+3,y+3);
        O.costSquares.add(tempArr);
        num++;
        }else if(O.getPoint(x-3,y-3,false)==-1&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==0)
            {
            
            MyObject tempArr = new MyObject(x+2,y+2);
            O.costSquares.add(tempArr);
            num++;}
        else if (O.getPoint(x-4,y-4,false)==-1&&O.getPoint(x-3,y-3)==1&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==0){
            
                MyObject tempArr = new MyObject(x+1,y+1);
                O.costSquares.add(tempArr);
                num++;
            }
        
        
        
        //XOOOO● 6 comp blocked four

        if (O.getPoint(x-1,y)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)==1&&O.getPoint(x+4,y,false)==-1)
            {MyObject tempArr = new MyObject(x-1,y);
            O.costSquares.add(tempArr);
            num++;}
        else if(O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y,false)==-1)
            {MyObject tempArr = new MyObject(x-2,y);
            O.costSquares.add(tempArr);
            num++;
            }
        else if(O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y,false)==-1)
            {MyObject tempArr = new MyObject(x-3,y);
            O.costSquares.add(tempArr);
            num++;}
        else if (O.getPoint(x-4,y)==0&&O.getPoint(x-3,y)==1&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y,false)==-1){
            MyObject tempArr = new MyObject(x-4,y);
            O.costSquares.add(tempArr);
            num++;
        }
        
        if (O.getPoint(x,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)==1&&O.getPoint(x,y+4,false)==-1)
        {MyObject tempArr = new MyObject(x,y-1);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3,false)==-1)
        {MyObject tempArr = new MyObject(x,y-2);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x,y-3)==0&&O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2,false)==-1)
            {MyObject tempArr = new MyObject(x,y-3);
            O.costSquares.add(tempArr);
            num++;}
        else if(O.getPoint(x,y-4)==0&&O.getPoint(x,y-3)==1&&O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1,false)==-1){
            MyObject tempArr = new MyObject(x,y-4);
            O.costSquares.add(tempArr);
            num++;
        }
        
        if (O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)==1&&O.getPoint(x+4,y-4,false)==-1)
        {MyObject tempArr = new MyObject(x-1,y+1);
        O.costSquares.add(tempArr);
        num++;}
        else if (O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3,false)==-1)
        {MyObject tempArr = new MyObject(x-2,y+2);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2,false)==-1)
            {MyObject tempArr = new MyObject(x-3,y+3);
            O.costSquares.add(tempArr);
            num++;
            }
        else if (O.getPoint(x-4,y+4)==0&&O.getPoint(x-3,y+3)==1&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1,false)==-1){
            MyObject tempArr = new MyObject(x-4,y+4);
            O.costSquares.add(tempArr);
            num++;
        }
        
        if (O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)==1&&O.getPoint(x+4,y+4,false)==-1)
        {MyObject tempArr = new MyObject(x-1,y-1);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3,false)==-1)
        {MyObject tempArr = new MyObject(x-2,y-2);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2,false)==-1)
        {MyObject tempArr = new MyObject(x-3,y-3);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-4,y-4)==0&&O.getPoint(x-3,y-3)==1&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1,false)==-1){
            MyObject tempArr = new MyObject(x-4,y-4);
            O.costSquares.add(tempArr);
            num++;
        }
        
        
            
        
            //XOOOX 5 comp three
        if(extrabit[0]==false){
        if (O.getPoint(x-1,y)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)==0)
        {MyObject tempArr = new MyObject(x-1,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+3,y);
        O.costSquares.add(tempArr2);
        num++;
        extrabit[0]=true;}
        else if (O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==0)
        {MyObject tempArr = new MyObject(x-2,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+2,y);
        O.costSquares.add(tempArr2);
        num++;
        extrabit[0]=true;}
        else if(O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==0){
            MyObject tempArr = new MyObject(x-3,y);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+1,y);
            O.costSquares.add(tempArr2);
            num++;
            extrabit[0]=true;}
        }
        if(extrabit[1]==false){
        if (O.getPoint(x,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)==0)
        {MyObject tempArr = new MyObject(x,y-1);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x,y+3);
        O.costSquares.add(tempArr2);
        num++;
        extrabit[1]=true;}
        else if (O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==0)
        {MyObject tempArr = new MyObject(x,y-2);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x,y+2);
        O.costSquares.add(tempArr2);
        num++;
        extrabit[1]=true;}
        else if (O.getPoint(x,y-3)==0&&O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==0){
            MyObject tempArr = new MyObject(x,y-3);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x,y+1);
            O.costSquares.add(tempArr2);
            num++;
            extrabit[1]=true;}
        }
        
        if(extrabit[2]==false){
        if (O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)==0)
        {MyObject tempArr = new MyObject(x-1,y+1);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+3,y-3);
        O.costSquares.add(tempArr2);
        num++;
        extrabit[2]=true;}
        else if (O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==0)
        {MyObject tempArr = new MyObject(x-2,y+2);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+2,y-2);
        O.costSquares.add(tempArr2);
        num++;
        extrabit[2]=true;}
        else if(O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==0){
            MyObject tempArr = new MyObject(x-3,y+3);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+1,y-1);
            O.costSquares.add(tempArr2);
            num++;
            extrabit[2]=true;}
        }
        
        if(extrabit[3]==false){
        if (O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)==0)
        {MyObject tempArr = new MyObject(x-1,y-1);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+3,y+3);
        O.costSquares.add(tempArr2);
        num++;
        extrabit[3]=true;}
        else if (O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==0)
        {MyObject tempArr = new MyObject(x-2,y-2);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+2,y+2);
        O.costSquares.add(tempArr2);
        num++;
        extrabit[3]=true;}
        else if(O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==0){
            MyObject tempArr = new MyObject(x-3,y-3);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+1,y+1);
            O.costSquares.add(tempArr2);
            num++;
            extrabit[3]=true;}
        }
        
        //XOXOOX 6 comp broken three
        if(extrabit[0]=false){
        if (O.getPoint(x-1,y)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)==1&&O.getPoint(x+4,y)==0)
        {MyObject tempArr = new MyObject(x-1,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+1,y);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+4,y);
        O.costSquares.add(tempArr3);
        num++;
        extrabit[0]=true;}
        else if (O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==0)
        {MyObject tempArr = new MyObject(x-3,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x-1,y);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+2,y);
        O.costSquares.add(tempArr3);
        num++;
        extrabit[0]=true;}
        
        else if(O.getPoint(x-4,y)==0&&O.getPoint(x-3,y)==1&&O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==0){
            MyObject tempArr = new MyObject(x-4,y);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x-2,y);
            O.costSquares.add(tempArr2);
            MyObject tempArr3 = new MyObject(x+1,y);
            O.costSquares.add(tempArr3);
            num++;
        extrabit[0]=true;}
        }
        if(extrabit[1]=false){
        if (O.getPoint(x,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)==1&&O.getPoint(x,y+4)==0)
                {MyObject tempArr = new MyObject(x,y-1);
                O.costSquares.add(tempArr);
                MyObject tempArr2 = new MyObject(x,y+1);
                O.costSquares.add(tempArr2);
                MyObject tempArr3 = new MyObject(x,y+4);
                O.costSquares.add(tempArr3);
                num++;
                extrabit[1]=true;}
        else if (O.getPoint(x,y-3)==0&&O.getPoint(x,y+2)==1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==0)
        {MyObject tempArr = new MyObject(x,y-3);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x,y-1);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x,y+2);
        O.costSquares.add(tempArr3);
        num++;
        extrabit[1]=true;}
        else if(O.getPoint(x,y-4)==0&&O.getPoint(x,y+3)==1&&O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==0){
            MyObject tempArr = new MyObject(x,y-4);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x,y-2);
            O.costSquares.add(tempArr2);
            MyObject tempArr3 = new MyObject(x,y+1);
            O.costSquares.add(tempArr3);
            num++;
            extrabit[1]=true;}
        }
        if(extrabit[2]=false){
        if (O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)==1&&O.getPoint(x+4,y-4)==0)
                {MyObject tempArr = new MyObject(x-1,y+1);
                O.costSquares.add(tempArr);
                MyObject tempArr2 = new MyObject(x+1,y-1);
                O.costSquares.add(tempArr2);
                MyObject tempArr3 = new MyObject(x+4,y-4);
                O.costSquares.add(tempArr3);
                num++;
                extrabit[2]=true;}
        else if (O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==0)
        {MyObject tempArr = new MyObject(x-3,y+3);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x-1,y+1);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+2,y-2);
        O.costSquares.add(tempArr3);
        num++;
        extrabit[2]=true;}
        else if (O.getPoint(x-4,y+4)==0&&O.getPoint(x-3,y+3)==1&&O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==0){
            MyObject tempArr = new MyObject(x-4,y+4);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x-2,y+2);
            O.costSquares.add(tempArr2);
            MyObject tempArr3 = new MyObject(x+1,y-1);
            O.costSquares.add(tempArr3);
            num++;
            extrabit[2]=true;}
        
        }
        if(extrabit[3]=false){
        if (O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)==1&&O.getPoint(x+4,y+4)==0)
        {MyObject tempArr = new MyObject(x-1,y-1);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+1,y+1);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+4,y+4);
        O.costSquares.add(tempArr3);
        num++;
        extrabit[3]=true;}
        else if (O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==0)
        {MyObject tempArr = new MyObject(x-3,y-3);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x-1,y-1);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+2,y+2);
        O.costSquares.add(tempArr3);
        num++;
        extrabit[3]=true;}
        else if(O.getPoint(x-4,y-4)==0&&O.getPoint(x-3,y-3)==1&&O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==0){
            MyObject tempArr = new MyObject(x-4,y-4);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x-2,y-2);
            O.costSquares.add(tempArr2);
            MyObject tempArr3 = new MyObject(x+1,y+1);
            O.costSquares.add(tempArr3);
            num++;
            extrabit[3]=true;}
        
        }
        
        //XOOXOX 6 comp broken three
        if(extrabit[0]=false){
        if (O.getPoint(x-1,y)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==1&&O.getPoint(x+2,y)==0&&O.getPoint(x+3,y)==1&&O.getPoint(x+4,y)==0)
        {MyObject tempArr = new MyObject(x-1,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+2,y);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+4,y);
        O.costSquares.add(tempArr3);
        num++;}
        else if (O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==1&&O.getPoint(x+3,y)==0)
        {MyObject tempArr = new MyObject(x-2,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+1,y);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+3,y);
        O.costSquares.add(tempArr3);
        num++;}
        else if (O.getPoint(x-4,y)==0&&O.getPoint(x-3,y)==1&&O.getPoint(x-2,y)==1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y)==0){
            MyObject tempArr = new MyObject(x-4,y);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x-1,y);
            O.costSquares.add(tempArr2);
            MyObject tempArr3 = new MyObject(x+1,y);
            O.costSquares.add(tempArr3);
            num++;}
        }
        if(extrabit[1]=false){
        if (O.getPoint(x,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==1&&O.getPoint(x,y+2)==0&&O.getPoint(x,y+3)==1&&O.getPoint(x,y+4)==0)
        {MyObject tempArr = new MyObject(x,y-1);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x,y+2);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x,y+4);
        O.costSquares.add(tempArr3);
        num++;}
        else if(O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==1&&O.getPoint(x,y+3)==0)
        {MyObject tempArr = new MyObject(x,y-2);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x,y+1);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x,y+3);
        O.costSquares.add(tempArr3);
        num++;}
        else if (O.getPoint(x,y-4)==0&&O.getPoint(x,y-3)==1&&O.getPoint(x,y-2)==1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x,y+1)==0){
            MyObject tempArr = new MyObject(x,y-4);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x,y-1);
            O.costSquares.add(tempArr2);
            MyObject tempArr3 = new MyObject(x,y+1);
            O.costSquares.add(tempArr3);
            num++;} 
        }
        if(extrabit[2]=false){
        
        if (O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==1&&O.getPoint(x+2,y-2)==0&&O.getPoint(x+3,y-3)==1&&O.getPoint(x+4,y-4)==0)
        {MyObject tempArr = new MyObject(x-1,y+1);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+2,y-2);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+4,y-4);
        O.costSquares.add(tempArr3);
        num++;}
        else if(O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==1&&O.getPoint(x+3,y-3)==0)
        {MyObject tempArr = new MyObject(x-2,y+2);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+1,y-1);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+3,y-3);
        O.costSquares.add(tempArr3);
        num++;}
        else if (O.getPoint(x-4,y+4)==0&&O.getPoint(x-3,y+3)==1&&O.getPoint(x-2,y+2)==1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y-1)==0){
            MyObject tempArr = new MyObject(x-4,y+4);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x-1,y+1);
            O.costSquares.add(tempArr2);
            MyObject tempArr3 = new MyObject(x+1,y-1);
            O.costSquares.add(tempArr3);
            num++;} 
        }
        if(extrabit[3]=false){
        if (O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==1&&O.getPoint(x+2,y+2)==0&&O.getPoint(x+3,y+3)==1&&O.getPoint(x+4,y+4)==0)
        {MyObject tempArr = new MyObject(x-1,y-1);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+2,y+2);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+4,y+4);
        O.costSquares.add(tempArr3);
        num++;
        }
        else if (O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==1&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==1&&O.getPoint(x+3,y+3)==0)
        {MyObject tempArr = new MyObject(x-2,y-2);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+1,y+1);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+3,y+3);
        O.costSquares.add(tempArr3);
        num++;}
        else if (O.getPoint(x-4,y-4)==0&&O.getPoint(x-3,y-3)==1&&O.getPoint(x-2,y-2)==1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==1&&O.getPoint(x+1,y+1)==0){
            MyObject tempArr = new MyObject(x-4,y-4);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x-1,y-1);
            O.costSquares.add(tempArr2);
            MyObject tempArr3 = new MyObject(x+1,y+1);
            O.costSquares.add(tempArr3);
            num++;} 
        }
        return num;
    }

    //threat을 판단하고  costsquare를 업데이트한다.(pyr)
    public static int threatCheck2(Node O, boolean pyr){
        int num = 0;
        boolean[] extrabit = {false,false,false,false};
        int x = O.getPosition()[0];
        int y = O.getPosition()[1];
        
        if (//row
                (O.getPoint(x-1,y)!=-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)==-1&&O.getPoint(x+5,y)!=-1)
                ||(O.getPoint(x-2,y)!=-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)!=-1)
                ||(O.getPoint(x-3,y)!=-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)!=-1)
                ||(O.getPoint(x-4,y)!=-1&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)!=-1)
                ||(O.getPoint(x-5,y)!=-1&&O.getPoint(x-4,y)==-1&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)!=-1)
    //col
                ||(O.getPoint(x,y-1)!=-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)==-1&&O.getPoint(x,y+5)!=-1)
                ||(O.getPoint(x,y-2)!=-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)!=-1)
                ||(O.getPoint(x,y-3)!=-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)!=-1)
                ||(O.getPoint(x,y-4)!=-1&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)!=-1)
                ||(O.getPoint(x,y-5)!=-1&&O.getPoint(x,y-4)==-1&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)!=-1)
    //diag
                ||(O.getPoint(x-1,y+1)!=-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)==-1&&O.getPoint(x+5,y-5)!=-1)
                ||(O.getPoint(x-2,y+2)!=-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)!=-1)
                ||(O.getPoint(x-3,y+3)!=-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)!=-1)
                ||(O.getPoint(x-4,y+4)!=-1&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)!=-1)
                ||(O.getPoint(x-5,y+5)!=-1&&O.getPoint(x-4,y+4)==-1&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)!=-1)
    //diag      
                ||(O.getPoint(x-1,y-1)!=-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)==-1&&O.getPoint(x+5,y+5)!=-1)
                ||(O.getPoint(x-2,y-2)!=-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)!=-1)
                ||(O.getPoint(x-3,y-3)!=-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)!=-1)
                ||(O.getPoint(x-4,y-4)!=-1&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)!=-1)
                ||(O.getPoint(x-5,y-5)!=-1&&O.getPoint(x-4,y-4)==-1&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)!=-1)

                ){

            return 3;
        }
        
        // XOOOOX unblocked four pyr
        if (O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)==0)
        {MyObject tempArr = new MyObject(x-1,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+4,y);
        O.costSquares.add(tempArr2);
        return 3;}
        else if (O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==0)
        {MyObject tempArr = new MyObject(x-2,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+3,y);
        O.costSquares.add(tempArr2);
        return 3;}
        else if(O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==0)
        {MyObject tempArr = new MyObject(x-3,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+2,y);
        O.costSquares.add(tempArr2);
        return 3;}
        else if (O.getPoint(x-4,y)==0&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0){
            MyObject tempArr = new MyObject(x-4,y);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+1,y);
            O.costSquares.add(tempArr2);
            return 3;
            }
        
        if (O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)==0)
        {MyObject tempArr = new MyObject(x,y-1);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x,y+4);
        O.costSquares.add(tempArr2);
        return 3;}
        else if (O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==0)
        {MyObject tempArr = new MyObject(x,y-2);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x,y+3);
        O.costSquares.add(tempArr2);
        return 3;}
        else if(O.getPoint(x,y-3)==0&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==0)
        {MyObject tempArr = new MyObject(x,y-3);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x,y+2);
        O.costSquares.add(tempArr2);
        return 3;}
        else if (O.getPoint(x,y-4)==0&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0){
            MyObject tempArr = new MyObject(x,y-4);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x,y+1);
            O.costSquares.add(tempArr2);
            return 3;}
        
        
            if (O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)==0)
                    {MyObject tempArr = new MyObject(x-1,y+1);
                    O.costSquares.add(tempArr);
                    MyObject tempArr2 = new MyObject(x+4,y-4);
                    O.costSquares.add(tempArr2);
                    return 3;}
            else if (O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==0)
            {MyObject tempArr = new MyObject(x-2,y+2);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+3,y-3);
            O.costSquares.add(tempArr2);
            return 3;}
            else if(O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==0)
            {MyObject tempArr = new MyObject(x-3,y+3);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+2,y-2);
            O.costSquares.add(tempArr2);
            return 3;}
            else if (O.getPoint(x-4,y+4)==0&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0){
                MyObject tempArr = new MyObject(x-4,y+4);
                O.costSquares.add(tempArr);
                MyObject tempArr2 = new MyObject(x+1,y-1);
                O.costSquares.add(tempArr2);
                return 3;}
            
            if (O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)==0)
            {MyObject tempArr = new MyObject(x-1,y-1);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+4,y+4);
            O.costSquares.add(tempArr2);
            return 3;}
            else if (O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==0)
            {MyObject tempArr = new MyObject(x-2,y-2);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+3,y+3);
            O.costSquares.add(tempArr2);
            return 3;}
            else if (O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==0)
            {MyObject tempArr = new MyObject(x-3,y-3);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+2,y+2);
            O.costSquares.add(tempArr2);
            return 3;}
            else if (O.getPoint(x-4,y-4)==0&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0){
                MyObject tempArr = new MyObject(x-4,y-4);
                O.costSquares.add(tempArr);
                MyObject tempArr2 = new MyObject(x+1,y+1);
                O.costSquares.add(tempArr2);
                return 3;}

        //OOOXO pyr, 1,1,1,0,1 
        
        if (O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==0&&O.getPoint(x+4,y)==-1)
        {MyObject tempArr = new MyObject(x+3,y);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x-1, y)==0) extrabit[0] = true;}
        
        else if (O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==0&&O.getPoint(x+3,y)==-1)
        {MyObject tempArr = new MyObject(x+2,y);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x-2, y)==0) extrabit[0] = true;}
        
        else if(O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==-1){
        MyObject tempArr = new MyObject(x+1,y);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x-3, y)==0) extrabit[0] = true;}
        
        else if(O.getPoint(x-4,y)==-1&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1){
            MyObject tempArr = new MyObject(x-1,y);
            O.costSquares.add(tempArr);
            num++;
            if(O.getPoint(x-5, y)==0) extrabit[0] = true;}
        
        if (O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==0&&O.getPoint(x,y+4)==-1)
        {MyObject tempArr = new MyObject(x,y+3);
        O.costSquares.add(tempArr);
        num++;if(O.getPoint(x, y-1)==0) extrabit[1] = true;}
        else if (O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==0&&O.getPoint(x,y+3)==-1)
        {MyObject tempArr = new MyObject(x,y+2);
        O.costSquares.add(tempArr);
        num++;if(O.getPoint(x, y-2)==0) extrabit[1] = true;}
        else if(O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==-1){
        MyObject tempArr = new MyObject(x,y+1);
        O.costSquares.add(tempArr);
        num++;if(O.getPoint(x, y-3)==0) extrabit[1] = true;}
        else if(O.getPoint(x,y-4)==-1&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1){
            MyObject tempArr = new MyObject(x,y-1);
            O.costSquares.add(tempArr);
            num++;if(O.getPoint(x, y-5)==0) extrabit[1] = true;}
        
        if (O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==0&&O.getPoint(x+4,y-4)==-1)
        {MyObject tempArr = new MyObject(x+3,y-3);
        O.costSquares.add(tempArr);
        num++;if(O.getPoint(x-1, y+1)==0) extrabit[2] = true;}
        else if (O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==0&&O.getPoint(x+3,y-3)==-1)
        {MyObject tempArr = new MyObject(x+2,y-2);
        O.costSquares.add(tempArr);
        num++;if(O.getPoint(x-2, y+2)==0) extrabit[2] = true;}
        else if(O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==-1){
        MyObject tempArr = new MyObject(x+1,y-1);
        O.costSquares.add(tempArr);
        num++;if(O.getPoint(x-3, y+3)==0) extrabit[2] = true;}
        else if(O.getPoint(x-4,y+4)==-1&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1){
            MyObject tempArr = new MyObject(x-1,y+1);
            O.costSquares.add(tempArr);
            num++;if(O.getPoint(x-5, y+5)==0) extrabit[2] = true;}
        
        if (O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==0&&O.getPoint(x+4,y+4)==-1)
        {MyObject tempArr = new MyObject(x+3,y+3);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x-1, y-1)==0) extrabit[3] = true;}
        else if (O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==0&&O.getPoint(x+3,y+3)==-1)
        {MyObject tempArr = new MyObject(x+2,y+2);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x-2, y-2)==0) extrabit[3] = true;}
        else if(O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==-1){
        MyObject tempArr = new MyObject(x+1,y+1);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x-3, y-3)==0) extrabit[3] = true;}
        else if(O.getPoint(x-4,y-4)==-1&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1){
            MyObject tempArr = new MyObject(x-1,y-1);
            O.costSquares.add(tempArr);
            num++;
            if(O.getPoint(x-5, y-5)==0) extrabit[3] = true;}
    
        //.OOXOO.   pyr
        if(extrabit[0]=false){
        if (O.getPoint(x-1, y)!=-1&&O.getPoint(x+5, y)!=-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==0&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)==-1)
        {
            MyObject tempArr = new MyObject(x+2,y);
        O.costSquares.add(tempArr);
        num++;
        }
        else if (O.getPoint(x-2, y)!=-1&&O.getPoint(x+4, y)!=-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==-1)
        {MyObject tempArr = new MyObject(x+1,y);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-4, y)!=-1&&O.getPoint(x+2, y)!=-1&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1){
        MyObject tempArr = new MyObject(x-1,y);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-5, y)!=-1&&O.getPoint(x+1, y)!=-1&&O.getPoint(x-4,y)==-1&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1){
            MyObject tempArr = new MyObject(x-2,y);
            O.costSquares.add(tempArr);
            num++;}
        }
        if(extrabit[1]=false){
        if (O.getPoint(x, y-1)!=-1&&O.getPoint(x, y+5)!=-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==0&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)==-1)
        {MyObject tempArr = new MyObject(x,y+2);
        O.costSquares.add(tempArr);
        num++;}
        else if (O.getPoint(x, y-2)!=-1&&O.getPoint(x, y+4)!=-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==-1)
        {MyObject tempArr = new MyObject(x,y+1);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x, y-4)!=-1&&O.getPoint(x, y+2)!=-1&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1){
        MyObject tempArr = new MyObject(x,y-1);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x, y-5)!=-1&&O.getPoint(x, y+1)!=-1&&O.getPoint(x,y-4)==-1&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1){
            MyObject tempArr = new MyObject(x,y-2);
            O.costSquares.add(tempArr);
            num++;}
        }
        if(extrabit[2]=false){
        if (O.getPoint(x-1, y+1)!=-1&&O.getPoint(x+5, y-5)!=-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==0&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)==-1)
        {MyObject tempArr = new MyObject(x+2,y-2);
        O.costSquares.add(tempArr);
        num++;}
        else if (O.getPoint(x-2, y+2)!=-1&&O.getPoint(x+4, y-4)!=-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==-1)
        {MyObject tempArr = new MyObject(x+1,y-1);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-4, y+4)!=-1&&O.getPoint(x+2, y-2)!=-1&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1){
        MyObject tempArr = new MyObject(x-1,y+1);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-5, y+5)!=-1&&O.getPoint(x+1, y-1)!=-1&&O.getPoint(x-4,y+4)==-1&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1){
            MyObject tempArr = new MyObject(x-2,y+2);
            O.costSquares.add(tempArr);
            num++;}
        }
        if(extrabit[3]=false){
        if (O.getPoint(x-1, y-1)!=-1&&O.getPoint(x+5, y+5)!=-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==0&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)==-1)
        {MyObject tempArr = new MyObject(x+2,y+2);
        O.costSquares.add(tempArr);
        num++;}
        else if (O.getPoint(x-2, y-2)!=-1&&O.getPoint(x+4, y+4)!=-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==-1)
        {MyObject tempArr = new MyObject(x+1,y+1);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-4, y-4)!=-1&&O.getPoint(x+2, y+2)!=-1&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1){
        MyObject tempArr = new MyObject(x-1,y-1);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-5, y-5)!=-1&&O.getPoint(x+1, y+1)!=-1&&O.getPoint(x-4,y-4)==-1&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1){
            MyObject tempArr = new MyObject(x-2,y-2);
            O.costSquares.add(tempArr);
            num++;}
        }
//OXOOO pyr, 1,0,1,1,1 
                
        if(extrabit[0]=false){
        if (O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)==-1)
        {MyObject tempArr = new MyObject(x+1,y);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+5, y)==0) extrabit[0] = true;}
                
        else if(O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1){
        MyObject tempArr = new MyObject(x-1,y);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+3, y)==0) extrabit[0] = true;}
        
        else if (O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1)
        {MyObject tempArr = new MyObject(x-2,y);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+2, y)==0) extrabit[0] = true;}

        else if(O.getPoint(x-4,y)==-1&&O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1){
            MyObject tempArr = new MyObject(x-3,y);
            O.costSquares.add(tempArr);
            num++;
            if(O.getPoint(x+1, y)==0) extrabit[0] = true;}
        }
        
        if(extrabit[1]=false){
        
        if (O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)==-1)
        {MyObject tempArr = new MyObject(x,y+1);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x, y+5)==0) extrabit[1] = true;}
                
        else if(O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1){
        MyObject tempArr = new MyObject(x,y-1);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x, y+3)==0) extrabit[1] = true;}
        
        else if (O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1)
        {MyObject tempArr = new MyObject(x,y-2);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x, y+2)==0) extrabit[1] = true;}

        else if(O.getPoint(x,y-4)==-1&&O.getPoint(x,y-3)==0&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1){
            MyObject tempArr = new MyObject(x,y-3);
            O.costSquares.add(tempArr);
            num++;if(O.getPoint(x, y+1)==0) extrabit[1] = true;}
        }
        if(extrabit[2]=false){
        if (O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)==-1)
        {MyObject tempArr = new MyObject(x+1,y-1);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+5, y-5)==0) extrabit[2] = true;}
                
        else if(O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-2)==-1&&O.getPoint(x+2,y-2)==-1){
        MyObject tempArr = new MyObject(x-1,y+1);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+3, y-3)==0) extrabit[2] = true;}
        
        else if (O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1)
        {MyObject tempArr = new MyObject(x-2,y+2);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+2, y-2)==0) extrabit[2] = true;}

        else if(O.getPoint(x-4,y+4)==-1&&O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1){
            MyObject tempArr = new MyObject(x-3,y+3);
            O.costSquares.add(tempArr);
            num++;
            if(O.getPoint(x+1, y-1)==0) extrabit[2] = true;}
        
        }
        if(extrabit[3]=false){
        if (O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)==-1)
        {MyObject tempArr = new MyObject(x+1,y+1);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+5, y+5)==0) extrabit[3] = true;}
                
        else if(O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1){
        MyObject tempArr = new MyObject(x-1,y-1);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+3, y+3)==0) extrabit[3] = true;}
        
        else if (O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1)
        {MyObject tempArr = new MyObject(x-2,y-2);
        O.costSquares.add(tempArr);
        num++;
        if(O.getPoint(x+2, y+2)==0) extrabit[3] = true;}

        else if(O.getPoint(x-4,y-4)==-1&&O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1){
            MyObject tempArr = new MyObject(x-3,y-3);
            O.costSquares.add(tempArr);
            num++;
            if(O.getPoint(x+1, y+1)==0) extrabit[3] = true;}
        }
        
        //●OOOOX 6 pyr blocked four
        
        if ((O.getPoint(x-1,y,false)==1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)==0))
        {
            MyObject tempArr = new MyObject(x+4,y);
            O.costSquares.add(tempArr);         
            num++;
        }else if (O.getPoint(x-2,y,false)==1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==0){
            MyObject tempArr = new MyObject(x+3,y);
            O.costSquares.add(tempArr);
            num++;
        }else if(O.getPoint(x-3,y,false)==1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==0)
        {
            MyObject tempArr = new MyObject(x+2,y);
            O.costSquares.add(tempArr);
            num++;
        }
        else if (O.getPoint(x-4,y,false)==1&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0)
        {
            MyObject tempArr = new MyObject(x+1,y);
            O.costSquares.add(tempArr);
            num++;
        }
        
        
        if ((O.getPoint(x,y-1,false)==1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)==0)
            ){
            MyObject tempArr = new MyObject(x,y+4);
            O.costSquares.add(tempArr);
            num++;
        }else if(O.getPoint(x,y-2,false)==1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==0)
        {
            MyObject tempArr = new MyObject(x,y+3);
            O.costSquares.add(tempArr);
            num++;
        }
        else if(O.getPoint(x,y-3,false)==1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==0)
        {MyObject tempArr = new MyObject(x,y+2);
        O.costSquares.add(tempArr);
        num++;
        }
        else if(O.getPoint(x,y-4,false)==1&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0)
        {
            MyObject tempArr = new MyObject(x,y+1);
            O.costSquares.add(tempArr);
            num++;              
        }

        
        if (O.getPoint(x-1,y+1,false)==1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)==0)
                {MyObject tempArr = new MyObject(x+4,y-4);
                O.costSquares.add(tempArr);
                num++;}
        else if 
                (O.getPoint(x-2,y+2,false)==1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==0)
        {
            MyObject tempArr = new MyObject(x+3,y-3);
            O.costSquares.add(tempArr);
            num++;
        }
            else if (O.getPoint(x-3,y+3,false)==1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==0)
            {MyObject tempArr = new MyObject(x+2,y-2);
            O.costSquares.add(tempArr);
            num++;}
        
                else if (O.getPoint(x-4,y+4,false)==1&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0)
            {MyObject tempArr = new MyObject(x+1,y-1);
            O.costSquares.add(tempArr);
            num++;}
        
                
        if (O.getPoint(x-1,y-1,false)==1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)==0)
        {MyObject tempArr = new MyObject(x+4,y+4);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-2,y-2,false)==1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==0)
        {MyObject tempArr = new MyObject(x+3,y+3);
        O.costSquares.add(tempArr);
        num++;
        }else if(O.getPoint(x-3,y-3,false)==1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==0)
            {
            
            MyObject tempArr = new MyObject(x+2,y+2);
            O.costSquares.add(tempArr);
            num++;}
        else if (O.getPoint(x-4,y-4,false)==1&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0){
            
                MyObject tempArr = new MyObject(x+1,y+1);
                O.costSquares.add(tempArr);
                num++;
            }
        
        
        
        //XOOOO● 6 pyr blocked four

        if (O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y,false)==1)
            {MyObject tempArr = new MyObject(x-1,y);
            O.costSquares.add(tempArr);
            num++;}
        else if(O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y,false)==1)
            {MyObject tempArr = new MyObject(x-2,y);
            O.costSquares.add(tempArr);
            num++;
            }
        else if(O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y,false)==1)
            {MyObject tempArr = new MyObject(x-3,y);
            O.costSquares.add(tempArr);
            num++;}
        else if (O.getPoint(x-4,y)==0&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y,false)==1){
            MyObject tempArr = new MyObject(x-4,y);
            O.costSquares.add(tempArr);
            num++;
        }
        
        if (O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4,false)==1)
        {MyObject tempArr = new MyObject(x,y-1);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3,false)==1)
        {MyObject tempArr = new MyObject(x,y-2);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x,y-3)==0&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2,false)==1)
            {MyObject tempArr = new MyObject(x,y-3);
            O.costSquares.add(tempArr);
            num++;}
        else if(O.getPoint(x,y-4)==0&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1,false)==1){
            MyObject tempArr = new MyObject(x,y-4);
            O.costSquares.add(tempArr);
            num++;
        }
        
        if (O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4,false)==1)
        {MyObject tempArr = new MyObject(x-1,y+1);
        O.costSquares.add(tempArr);
        num++;}
        else if (O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3,false)==1)
        {MyObject tempArr = new MyObject(x-2,y+2);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2,false)==1)
            {MyObject tempArr = new MyObject(x-3,y+3);
            O.costSquares.add(tempArr);
            num++;
            }
        else if (O.getPoint(x-4,y+4)==0&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1,false)==1){
            MyObject tempArr = new MyObject(x-4,y+4);
            O.costSquares.add(tempArr);
            num++;
        }
        
        if (O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4,false)==1)
        {MyObject tempArr = new MyObject(x-1,y-1);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3,false)==1)
        {MyObject tempArr = new MyObject(x-2,y-2);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2,false)==1)
        {MyObject tempArr = new MyObject(x-3,y-3);
        O.costSquares.add(tempArr);
        num++;}
        else if(O.getPoint(x-4,y-4)==0&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1,false)==1){
            MyObject tempArr = new MyObject(x-4,y-4);
            O.costSquares.add(tempArr);
            num++;
        }
        
        
            
        
            //XOOOX 5 pyr three
        if(extrabit[0]==false){
        if (O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==0)
        {MyObject tempArr = new MyObject(x-1,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+3,y);
        O.costSquares.add(tempArr2);
        num++;
        extrabit[0]=true;}
        else if (O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==0)
        {MyObject tempArr = new MyObject(x-2,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+2,y);
        O.costSquares.add(tempArr2);
        num++;
        extrabit[0]=true;}
        else if(O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0){
            MyObject tempArr = new MyObject(x-3,y);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+1,y);
            O.costSquares.add(tempArr2);
            num++;
            extrabit[0]=true;}
        }
        if(extrabit[1]==false){
        if (O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==0)
        {MyObject tempArr = new MyObject(x,y-1);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x,y+3);
        O.costSquares.add(tempArr2);
        num++;
        extrabit[1]=true;}
        else if (O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==0)
        {MyObject tempArr = new MyObject(x,y-2);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x,y+2);
        O.costSquares.add(tempArr2);
        num++;
        extrabit[1]=true;}
        else if (O.getPoint(x,y-3)==0&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0){
            MyObject tempArr = new MyObject(x,y-3);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x,y+1);
            O.costSquares.add(tempArr2);
            num++;
            extrabit[1]=true;}
        }
        
        if(extrabit[2]==false){
        if (O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==0)
        {MyObject tempArr = new MyObject(x-1,y+1);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+3,y-3);
        O.costSquares.add(tempArr2);
        num++;
        extrabit[2]=true;}
        else if (O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==0)
        {MyObject tempArr = new MyObject(x-2,y+2);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+2,y-2);
        O.costSquares.add(tempArr2);
        num++;
        extrabit[2]=true;}
        else if(O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0){
            MyObject tempArr = new MyObject(x-3,y+3);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+1,y-1);
            O.costSquares.add(tempArr2);
            num++;
            extrabit[2]=true;}
        }
        
        if(extrabit[3]==false){
        if (O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==0)
        {MyObject tempArr = new MyObject(x-1,y-1);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+3,y+3);
        O.costSquares.add(tempArr2);
        num++;
        extrabit[3]=true;}
        else if (O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==0)
        {MyObject tempArr = new MyObject(x-2,y-2);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+2,y+2);
        O.costSquares.add(tempArr2);
        num++;
        extrabit[3]=true;}
        else if(O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0){
            MyObject tempArr = new MyObject(x-3,y-3);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x+1,y+1);
            O.costSquares.add(tempArr2);
            num++;
            extrabit[3]=true;}
        }
        
        //XOXOOX 6 pyr broken three
        if(extrabit[0]=false){
        if (O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)==0)
        {MyObject tempArr = new MyObject(x-1,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+1,y);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+4,y);
        O.costSquares.add(tempArr3);
        num++;
        extrabit[0]=true;}
        else if (O.getPoint(x-3,y)==0&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==0)
        {MyObject tempArr = new MyObject(x-3,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x-1,y);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+2,y);
        O.costSquares.add(tempArr3);
        num++;
        extrabit[0]=true;}
        
        else if(O.getPoint(x-4,y)==0&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0){
            MyObject tempArr = new MyObject(x-4,y);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x-2,y);
            O.costSquares.add(tempArr2);
            MyObject tempArr3 = new MyObject(x+1,y);
            O.costSquares.add(tempArr3);
            num++;
        extrabit[0]=true;}
        }
        if(extrabit[1]=false){
        if (O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)==0)
                {MyObject tempArr = new MyObject(x,y-1);
                O.costSquares.add(tempArr);
                MyObject tempArr2 = new MyObject(x,y+1);
                O.costSquares.add(tempArr2);
                MyObject tempArr3 = new MyObject(x,y+4);
                O.costSquares.add(tempArr3);
                num++;
                extrabit[1]=true;}
        else if (O.getPoint(x,y-3)==0&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==0)
        {MyObject tempArr = new MyObject(x,y-3);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x,y-1);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x,y+2);
        O.costSquares.add(tempArr3);
        num++;
        extrabit[1]=true;}
        else if(O.getPoint(x,y-4)==0&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0){
            MyObject tempArr = new MyObject(x,y-4);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x,y-2);
            O.costSquares.add(tempArr2);
            MyObject tempArr3 = new MyObject(x,y+1);
            O.costSquares.add(tempArr3);
            num++;
            extrabit[1]=true;}
        }
        if(extrabit[2]=false){
        if (O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)==0)
                {MyObject tempArr = new MyObject(x-1,y+1);
                O.costSquares.add(tempArr);
                MyObject tempArr2 = new MyObject(x+1,y-1);
                O.costSquares.add(tempArr2);
                MyObject tempArr3 = new MyObject(x+4,y-4);
                O.costSquares.add(tempArr3);
                num++;
                extrabit[2]=true;}
        else if (O.getPoint(x-3,y+3)==0&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==0)
        {MyObject tempArr = new MyObject(x-3,y+3);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x-1,y+1);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+2,y-2);
        O.costSquares.add(tempArr3);
        num++;
        extrabit[2]=true;}
        else if (O.getPoint(x-4,y+4)==0&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0){
            MyObject tempArr = new MyObject(x-4,y+4);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x-2,y+2);
            O.costSquares.add(tempArr2);
            MyObject tempArr3 = new MyObject(x+1,y-1);
            O.costSquares.add(tempArr3);
            num++;
            extrabit[2]=true;}
        
        }
        if(extrabit[3]=false){
        if (O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)==0)
        {MyObject tempArr = new MyObject(x-1,y-1);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+1,y+1);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+4,y+4);
        O.costSquares.add(tempArr3);
        num++;
        extrabit[3]=true;}
        else if (O.getPoint(x-3,y-3)==0&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==0)
        {MyObject tempArr = new MyObject(x-3,y-3);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x-1,y-1);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+2,y+2);
        O.costSquares.add(tempArr3);
        num++;
        extrabit[3]=true;}
        else if(O.getPoint(x-4,y-4)==0&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0){
            MyObject tempArr = new MyObject(x-4,y-4);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x-2,y-2);
            O.costSquares.add(tempArr2);
            MyObject tempArr3 = new MyObject(x+1,y+1);
            O.costSquares.add(tempArr3);
            num++;
            extrabit[3]=true;}
        
        }
        
        //XOOXOX 6 pyr broken three
        if(extrabit[0]=false){
        if (O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==-1&&O.getPoint(x+2,y)==0&&O.getPoint(x+3,y)==-1&&O.getPoint(x+4,y)==0)
        {MyObject tempArr = new MyObject(x-1,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+2,y);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+4,y);
        O.costSquares.add(tempArr3);
        num++;}
        else if (O.getPoint(x-2,y)==0&&O.getPoint(x-1,y)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0&&O.getPoint(x+2,y)==-1&&O.getPoint(x+3,y)==0)
        {MyObject tempArr = new MyObject(x-2,y);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+1,y);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+3,y);
        O.costSquares.add(tempArr3);
        num++;}
        else if (O.getPoint(x-4,y)==0&&O.getPoint(x-3,y)==-1&&O.getPoint(x-2,y)==-1&&O.getPoint(x-1,y)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y)==0){
            MyObject tempArr = new MyObject(x-4,y);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x-1,y);
            O.costSquares.add(tempArr2);
            MyObject tempArr3 = new MyObject(x+1,y);
            O.costSquares.add(tempArr3);
            num++;}
        }
        if(extrabit[1]=false){
        if (O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==-1&&O.getPoint(x,y+2)==0&&O.getPoint(x,y+3)==-1&&O.getPoint(x,y+4)==0)
        {MyObject tempArr = new MyObject(x,y-1);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x,y+2);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x,y+4);
        O.costSquares.add(tempArr3);
        num++;}
        else if(O.getPoint(x,y-2)==0&&O.getPoint(x,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0&&O.getPoint(x,y+2)==-1&&O.getPoint(x,y+3)==0)
        {MyObject tempArr = new MyObject(x,y-2);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x,y+1);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x,y+3);
        O.costSquares.add(tempArr3);
        num++;}
        else if (O.getPoint(x,y-4)==0&&O.getPoint(x,y-3)==-1&&O.getPoint(x,y-2)==-1&&O.getPoint(x,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x,y+1)==0){
            MyObject tempArr = new MyObject(x,y-4);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x,y-1);
            O.costSquares.add(tempArr2);
            MyObject tempArr3 = new MyObject(x,y+1);
            O.costSquares.add(tempArr3);
            num++;} 
        }
        if(extrabit[2]=false){
        
        if (O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==-1&&O.getPoint(x+2,y-2)==0&&O.getPoint(x+3,y-3)==-1&&O.getPoint(x+4,y-4)==0)
        {MyObject tempArr = new MyObject(x-1,y+1);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+2,y-2);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+4,y-4);
        O.costSquares.add(tempArr3);
        num++;}
        else if(O.getPoint(x-2,y+2)==0&&O.getPoint(x-1,y+1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0&&O.getPoint(x+2,y-2)==-1&&O.getPoint(x+3,y-3)==0)
        {MyObject tempArr = new MyObject(x-2,y+2);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+1,y-1);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+3,y-3);
        O.costSquares.add(tempArr3);
        num++;}
        else if (O.getPoint(x-4,y+4)==0&&O.getPoint(x-3,y+3)==-1&&O.getPoint(x-2,y+2)==-1&&O.getPoint(x-1,y+1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y-1)==0){
            MyObject tempArr = new MyObject(x-4,y+4);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x-1,y+1);
            O.costSquares.add(tempArr2);
            MyObject tempArr3 = new MyObject(x+1,y-1);
            O.costSquares.add(tempArr3);
            num++;} 
        }
        if(extrabit[3]=false){
        if (O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==-1&&O.getPoint(x+2,y+2)==0&&O.getPoint(x+3,y+3)==-1&&O.getPoint(x+4,y+4)==0)
        {MyObject tempArr = new MyObject(x-1,y-1);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+2,y+2);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+4,y+4);
        O.costSquares.add(tempArr3);
        num++;
        }
        else if (O.getPoint(x-2,y-2)==0&&O.getPoint(x-1,y-1)==-1&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0&&O.getPoint(x+2,y+2)==-1&&O.getPoint(x+3,y+3)==0)
        {MyObject tempArr = new MyObject(x-2,y-2);
        O.costSquares.add(tempArr);
        MyObject tempArr2 = new MyObject(x+1,y+1);
        O.costSquares.add(tempArr2);
        MyObject tempArr3 = new MyObject(x+3,y+3);
        O.costSquares.add(tempArr3);
        num++;}
        else if (O.getPoint(x-4,y-4)==0&&O.getPoint(x-3,y-3)==-1&&O.getPoint(x-2,y-2)==-1&&O.getPoint(x-1,y-1)==0&&O.getPoint(x,y)==-1&&O.getPoint(x+1,y+1)==0){
            MyObject tempArr = new MyObject(x-4,y-4);
            O.costSquares.add(tempArr);
            MyObject tempArr2 = new MyObject(x-1,y-1);
            O.costSquares.add(tempArr2);
            MyObject tempArr3 = new MyObject(x+1,y+1);
            O.costSquares.add(tempArr3);
            num++;} 
        }
        return num;
    }
    public static void print_exe(){
        int tempi = MainCell.getPosition()[0];
        int tempj = MainCell.getPosition()[1];
        
        String j="A8";
        
        if (tempj ==0){
            if (tempi ==0){
                j="A15";
            }else if(tempi==1){
                j="A14";
            }else if(tempi==2){
                j="A13";
            }else if(tempi==3){
                j="A12";
            }else if(tempi==4){
                j="A11";
            }else if(tempi==5){
                j="A10";
            }else if(tempi==6){
                j="A9";
            }else if(tempi==7){
                j="A8";
            }else if(tempi==8){
                j="A7";
            }else if(tempi==9){
                j="A6";
            }else if(tempi==10){
                j="A5";
            }else if(tempi==11){
                j="A4";
            }else if(tempi==12){
                j="A3";
            }else if(tempi==13){
                j="A2";
            }else if(tempi==14){
                j="A1";
            }

        }else if(tempj==1){
            if (tempi ==0){
                j="B15";
            }else if(tempi==1){
                j="B14";
            }else if(tempi==2){
                j="B13";
            }else if(tempi==3){
                j="B12";
            }else if(tempi==4){
                j="B11";
            }else if(tempi==5){
                j="B10";
            }else if(tempi==6){
                j="B9";
            }else if(tempi==7){
                j="B8";
            }else if(tempi==8){
                j="B7";
            }else if(tempi==9){
                j="B6";
            }else if(tempi==10){
                j="B5";
            }else if(tempi==11){
                j="B4";
            }else if(tempi==12){
                j="B3";
            }else if(tempi==13){
                j="B2";
            }else if(tempi==14){
                j="B1";
            }
        }else if(tempj==2){
            if (tempi ==0){
                j="C15";
            }else if(tempi==1){
                j="C14";
            }else if(tempi==2){
                j="C13";
            }else if(tempi==3){
                j="C12";
            }else if(tempi==4){
                j="C11";
            }else if(tempi==5){
                j="C10";
            }else if(tempi==6){
                j="C9";
            }else if(tempi==7){
                j="C8";
            }else if(tempi==8){
                j="C7";
            }else if(tempi==9){
                j="C6";
            }else if(tempi==10){
                j="C5";
            }else if(tempi==11){
                j="C4";
            }else if(tempi==12){
                j="C3";
            }else if(tempi==13){
                j="C2";
            }else if(tempi==14){
                j="C1";
            }
        }else if(tempj==3){
            if (tempi ==0){
                j="D15";
            }else if(tempi==1){
                j="D14";
            }else if(tempi==2){
                j="D13";
            }else if(tempi==3){
                j="D12";
            }else if(tempi==4){
                j="D11";
            }else if(tempi==5){
                j="D10";
            }else if(tempi==6){
                j="D9";
            }else if(tempi==7){
                j="D8";
            }else if(tempi==8){
                j="D7";
            }else if(tempi==9){
                j="D6";
            }else if(tempi==10){
                j="D5";
            }else if(tempi==11){
                j="D4";
            }else if(tempi==12){
                j="D3";
            }else if(tempi==13){
                j="D2";
            }else if(tempi==14){
                j="D1";
            }
        }else if(tempj==4){
            if (tempi ==0){
                j="E15";
            }else if(tempi==1){
                j="E14";
            }else if(tempi==2){
                j="E13";
            }else if(tempi==3){
                j="E12";
            }else if(tempi==4){
                j="E11";
            }else if(tempi==5){
                j="E10";
            }else if(tempi==6){
                j="E9";
            }else if(tempi==7){
                j="E8";
            }else if(tempi==8){
                j="E7";
            }else if(tempi==9){
                j="E6";
            }else if(tempi==10){
                j="E5";
            }else if(tempi==11){
                j="E4";
            }else if(tempi==12){
                j="E3";
            }else if(tempi==13){
                j="E2";
            }else if(tempi==14){
                j="E1";
            }
        }else if(tempj==5){
            if (tempi ==0){
                j="F15";
            }else if(tempi==1){
                j="F14";
            }else if(tempi==2){
                j="F13";
            }else if(tempi==3){
                j="F12";
            }else if(tempi==4){
                j="F11";
            }else if(tempi==5){
                j="F10";
            }else if(tempi==6){
                j="F9";
            }else if(tempi==7){
                j="F8";
            }else if(tempi==8){
                j="F7";
            }else if(tempi==9){
                j="F6";
            }else if(tempi==10){
                j="F5";
            }else if(tempi==11){
                j="F4";
            }else if(tempi==12){
                j="F3";
            }else if(tempi==13){
                j="F2";
            }else if(tempi==14){
                j="F1";
            }
        }else if(tempj==6){
            if (tempi ==0){
                j="G15";
            }else if(tempi==1){
                j="G14";
            }else if(tempi==2){
                j="G13";
            }else if(tempi==3){
                j="G12";
            }else if(tempi==4){
                j="G11";
            }else if(tempi==5){
                j="G10";
            }else if(tempi==6){
                j="G9";
            }else if(tempi==7){
                j="G8";
            }else if(tempi==8){
                j="G7";
            }else if(tempi==9){
                j="G6";
            }else if(tempi==10){
                j="G5";
            }else if(tempi==11){
                j="G4";
            }else if(tempi==12){
                j="G3";
            }else if(tempi==13){
                j="G2";
            }else if(tempi==14){
                j="G1";
            }
        }else if(tempj==7){
            if (tempi ==0){
                j="H15";
            }else if(tempi==1){
                j="H14";
            }else if(tempi==2){
                j="H13";
            }else if(tempi==3){
                j="H12";
            }else if(tempi==4){
                j="H11";
            }else if(tempi==5){
                j="H10";
            }else if(tempi==6){
                j="H9";
            }else if(tempi==7){
                j="H8";
            }else if(tempi==8){
                j="H7";
            }else if(tempi==9){
                j="H6";
            }else if(tempi==10){
                j="H5";
            }else if(tempi==11){
                j="H4";
            }else if(tempi==12){
                j="H3";
            }else if(tempi==13){
                j="H2";
            }else if(tempi==14){
                j="H1";
            }
        }else if(tempj==8){
            if (tempi ==0){
                j="I15";
            }else if(tempi==1){
                j="I14";
            }else if(tempi==2){
                j="I13";
            }else if(tempi==3){
                j="I12";
            }else if(tempi==4){
                j="I11";
            }else if(tempi==5){
                j="I10";
            }else if(tempi==6){
                j="I9";
            }else if(tempi==7){
                j="I8";
            }else if(tempi==8){
                j="I7";
            }else if(tempi==9){
                j="I6";
            }else if(tempi==10){
                j="I5";
            }else if(tempi==11){
                j="I4";
            }else if(tempi==12){
                j="I3";
            }else if(tempi==13){
                j="I2";
            }else if(tempi==14){
                j="I1";
            }
        }else if(tempj==9){
            if (tempi ==0){
                j="J15";
            }else if(tempi==1){
                j="J14";
            }else if(tempi==2){
                j="J13";
            }else if(tempi==3){
                j="J12";
            }else if(tempi==4){
                j="J11";
            }else if(tempi==5){
                j="J10";
            }else if(tempi==6){
                j="J9";
            }else if(tempi==7){
                j="J8";
            }else if(tempi==8){
                j="J7";
            }else if(tempi==9){
                j="J6";
            }else if(tempi==10){
                j="J5";
            }else if(tempi==11){
                j="J4";
            }else if(tempi==12){
                j="J3";
            }else if(tempi==13){
                j="J2";
            }else if(tempi==14){
                j="J1";
            }
        }else if(tempj==10){
            if (tempi ==0){
                j="K15";
            }else if(tempi==1){
                j="K14";
            }else if(tempi==2){
                j="K13";
            }else if(tempi==3){
                j="K12";
            }else if(tempi==4){
                j="K11";
            }else if(tempi==5){
                j="K10";
            }else if(tempi==6){
                j="K9";
            }else if(tempi==7){
                j="K8";
            }else if(tempi==8){
                j="K7";
            }else if(tempi==9){
                j="K6";
            }else if(tempi==10){
                j="K5";
            }else if(tempi==11){
                j="K4";
            }else if(tempi==12){
                j="K3";
            }else if(tempi==13){
                j="K2";
            }else if(tempi==14){
                j="K1";
            }
        }else if(tempj==11){
            if (tempi ==0){
                j="L15";
            }else if(tempi==1){
                j="L14";
            }else if(tempi==2){
                j="L13";
            }else if(tempi==3){
                j="L12";
            }else if(tempi==4){
                j="L11";
            }else if(tempi==5){
                j="L10";
            }else if(tempi==6){
                j="L9";
            }else if(tempi==7){
                j="L8";
            }else if(tempi==8){
                j="L7";
            }else if(tempi==9){
                j="L6";
            }else if(tempi==10){
                j="L5";
            }else if(tempi==11){
                j="L4";
            }else if(tempi==12){
                j="L3";
            }else if(tempi==13){
                j="L2";
            }else if(tempi==14){
                j="L1";
            }
        }else if(tempj==12){
            if (tempi ==0){
                j="M15";
            }else if(tempi==1){
                j="M14";
            }else if(tempi==2){
                j="M13";
            }else if(tempi==3){
                j="M12";
            }else if(tempi==4){
                j="M11";
            }else if(tempi==5){
                j="M10";
            }else if(tempi==6){
                j="M9";
            }else if(tempi==7){
                j="M8";
            }else if(tempi==8){
                j="M7";
            }else if(tempi==9){
                j="M6";
            }else if(tempi==10){
                j="M5";
            }else if(tempi==11){
                j="M4";
            }else if(tempi==12){
                j="M3";
            }else if(tempi==13){
                j="M2";
            }else if(tempi==14){
                j="M1";
            }
        }else if(tempj==13){
            if (tempi ==0){
                j="N15";
            }else if(tempi==1){
                j="N14";
            }else if(tempi==2){
                j="N13";
            }else if(tempi==3){
                j="N12";
            }else if(tempi==4){
                j="N11";
            }else if(tempi==5){
                j="N10";
            }else if(tempi==6){
                j="N9";
            }else if(tempi==7){
                j="N8";
            }else if(tempi==8){
                j="N7";
            }else if(tempi==9){
                j="N6";
            }else if(tempi==10){
                j="N5";
            }else if(tempi==11){
                j="N4";
            }else if(tempi==12){
                j="N3";
            }else if(tempi==13){
                j="N2";
            }else if(tempi==14){
                j="N1";
            }
        }else if(tempj==14){
            if (tempi ==0){
                j="O15";
            }else if(tempi==1){
                j="O14";
            }else if(tempi==2){
                j="O13";
            }else if(tempi==3){
                j="O12";
            }else if(tempi==4){
                j="O11";
            }else if(tempi==5){
                j="O10";
            }else if(tempi==6){
                j="O9";
            }else if(tempi==7){
                j="O8";
            }else if(tempi==8){
                j="O7";
            }else if(tempi==9){
                j="O6";
            }else if(tempi==10){
                j="O5";
            }else if(tempi==11){
                j="O4";
            }else if(tempi==12){
                j="O3";
            }else if(tempi==13){
                j="O2";
            }else if(tempi==14){
                j="O1";
            }
        }
        
        System.out.println(j);

    }

}