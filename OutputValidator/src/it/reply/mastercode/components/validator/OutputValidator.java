package it.reply.mastercode.components.validator;

import it.reply.mastercode.components.office.graph.GraphEdge;
import it.reply.mastercode.components.office.graph.GraphWeight;
import it.reply.mastercode.components.office.reply.team.Employee;
import it.reply.mastercode.components.office.common.Points;
import it.reply.mastercode.components.office.customer.Problem;
import it.reply.mastercode.components.office.reply.team.Team;
import it.reply.mastercode.components.office.reply.team.TeamMember;
import it.reply.mastercode.components.office.customer.OfficeCustomer;
import it.reply.mastercode.components.office.reply.OfficeReply;
import it.reply.mastercode.components.misc.Constants;
import it.reply.mastercode.components.misc.IOUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jacopo Moscioni on 27/06/17.
 */
public class OutputValidator {
    private int customersNumber;
    private int replyNumber;
    private int problemsNumber;

    //private Map<String,Team> tMap = new HashMap<>();
    private List<Team> tList = new ArrayList<>();
    private Map<String,GraphEdge> eMap = new HashMap<>();
    private Map<String,Problem> pMap = new HashMap<>();
    private Map<String,OfficeReply> orMap = new HashMap<>();
    private Map<String,OfficeCustomer> ocMap = new HashMap<>();

    public static void main(String [] args){
        System.out.println("inizio");
        new OutputValidator();
        System.out.println("fine");
    }

    private OutputValidator() {
        Float points = new Float(Float.MAX_VALUE);
        if (parseInputFile() && parseOutputFile())
            points = performScore();
        System.out.println("your score is: " + points);
    }

    private boolean parseInputFile(){
        int lineCounter = 0;

        ClassLoader classLoader = getClass().getClassLoader();
        //List<String> input = IOUtils.readFile(Constants.INPUT_PATH_JACOPO_MAC);
        List<String> input = IOUtils.readFile(Constants.INPUT_PATH_JACOPO_WINDOWS);

        //List<String> input = IOUtils.readFile( classLoader.getResource("input.txt").getFile().replace("/","\\\\").replace("\\\\C:","C:"));


        String [] header = input.get(lineCounter++).split(Constants.SEPARATOR_SPACE);
        if (header.length == 3) {
            customersNumber = Integer.parseInt(header[0]);
            replyNumber = Integer.parseInt(header[1]);
            problemsNumber = Integer.parseInt(header[2]);
        }
        else {
            System.out.println("ERROR OCCURRED. INPUT FILE CORRUPTED ON LINE: "+(lineCounter-1));
            return false;
        }

        //READING EDGES
        for (int c = 0; c < customersNumber; c++){
            for (int R = 0; R < replyNumber; R++) {
                String[] edgeDescriptor = input.get(lineCounter++).split(Constants.SEPARATOR_SPACE);
                if (edgeDescriptor.length == 2) {
                    GraphWeight gw = new GraphWeight(Integer.parseInt(edgeDescriptor[0]), Integer.parseInt(edgeDescriptor[1]));
                    OfficeReply tempR = orMap.get(Constants.PREFIX_REPLY + R);
                    OfficeCustomer tempc = ocMap.get(Constants.PREFIX_CUSTOMER + c);
                    OfficeReply or;
                    OfficeCustomer oc;
                    if (tempR == null) {
                        OfficeReply newOne = new OfficeReply(Constants.PREFIX_REPLY + R);
                        orMap.put(Constants.PREFIX_REPLY + R, newOne);
                        or = newOne;
                    }
                    else {
                        or = tempR;
                    }

                    if (tempc == null) {
                        OfficeCustomer newOne = new OfficeCustomer(Constants.PREFIX_CUSTOMER + c);
                        ocMap.put(Constants.PREFIX_CUSTOMER + c, newOne);
                        oc = newOne;
                    }
                    else {
                        oc = tempc;
                    }
                    GraphEdge edge = new GraphEdge(gw,or,oc);
                    eMap.put(edge.getName(),edge);
                } else {
                    System.out.println("ERROR OCCURRED. INPUT FILE CORRUPTED ON LINE: " + (lineCounter - 1));
                    return false;
                }
            }
        }
        //FINISH EDGES

        //READING REPLY OFFICE DESCRIPTION
        if (orMap.size() != replyNumber){
            System.out.println("ERROR OCCURRED. INPUT FILE CORRUPTED. REPLY OFFICES COUNT IS NOT VALID");
            return false;
        }

        for (int R = 0; R < replyNumber; R++){
            String[] employeeLine = input.get(lineCounter++).split(Constants.SEPARATOR_SPACE);
            OfficeReply or = orMap.get(Constants.PREFIX_REPLY + R);
            if (employeeLine.length == 1){
                or.setNumeroDipendenti(Integer.parseInt(employeeLine[0]));
            }
            else {
                System.out.println("ERROR OCCURRED. INPUT FILE CORRUPTED ON LINE: " + (lineCounter - 1));
                return false;
            }
            //READING EMPLOYEES
            if (or.getNumeroDipendenti() > 0){
                ArrayList<Employee> employeeList = new ArrayList<>();
                for (int e = 0; e < or.getNumeroDipendenti(); e++){
                    String[] employeeDescriptor = input.get(lineCounter++).split(Constants.SEPARATOR_SPACE);
                    if (employeeDescriptor.length == 3) {
                        Points p = new Points(Integer.parseInt(employeeDescriptor[0]), Integer.parseInt(employeeDescriptor[1]));
                        Employee employee = new Employee(p, Integer.parseInt(employeeDescriptor[2]),or, Constants.PREFIX_EMPLOYEE + e);
                        //employeeList.add(employee);
                        or.getDipendentiLista().add(employee);
                        or.getDipendentiMap().put(employee.getName(),employee);
                    }
                    else{
                        System.out.println("ERROR OCCURRED. INPUT FILE CORRUPTED ON LINE: " + (lineCounter - 1));
                        return false;
                    }
                }
                //or.setDipendentiLista(employeeList);
                //orList.add(or);
                //orMap.put(or.getName(),or);
            }
            else{
                System.out.println("ERROR OCCURRED. INPUT FILE CORRUPTED ON LINE: " + (lineCounter - 1));
                return false;
            }
            //FINISH EMPLOYEES
        }

        //FINISH REPLY OFFICES

        //READING PROBLEMS

        //List<Problem> pList = new ArrayList<>();

        for (int p = 0; p < problemsNumber; p++) {
            String[] problemLine = input.get(lineCounter++).split(" ");
            if (problemLine.length == 5) {
                Integer softwareSkill = Integer.parseInt(problemLine[0]);
                Integer hardwareSkill = Integer.parseInt(problemLine[1]);
                Integer timeLeft = Integer.parseInt(problemLine[2]);
                Integer expectedCost = Integer.parseInt(problemLine[3]);
                Integer customerSource = Integer.parseInt(problemLine[4]) - 1;
                Points pt = new Points(softwareSkill,hardwareSkill);

                Problem problem = new Problem(pt, timeLeft, expectedCost, ocMap.get(Constants.PREFIX_CUSTOMER + customerSource), Constants.PREFIX_PROBLEM + p);
                //problemList.add(problem);
                pMap.put(problem.getName(),problem);
            }
            else{
                System.out.println("ERROR OCCURRED. INPUT FILE CORRUPTED ON LINE: " + (lineCounter - 1));
                return false;
            }
        }

        //FINISH PROBLEMS

        // eMap.get(0);
        return true;
    }

    private boolean parseOutputFile() {
        int lineCounter = 0;

        //List<String> output = IOUtils.readFile(Constants.OUTPUT_PATH_JACOPO_MAC);
        List<String> output = IOUtils.readFile(Constants.OUTPUT_PATH_JACOPO_WINDOWS);
        int numeroProblemi = output.size();
        if (numeroProblemi != pMap.size()) {
            System.out.println("ERROR OCCURRED. OUTPUT FILE CORRUPTED NOT OK. OUTPUT PROBLEMS NUMBER DOES NOT MATCH THE INPUT PROBLEMS NUMBER");
            return false;
        } else {
            for (int p = 0; p < numeroProblemi; p++) {
                String[] teamDescriptor = output.get(lineCounter++).split(Constants.SEPARATOR_SPACE);
                Problem problem = pMap.get(Constants.PREFIX_PROBLEM + p);
                if (problem == null) {
                    System.out.println("ERROR OCCURRED. OUTPUT FILE CORRUPTED NOT OK.PROBLEM INDEX IS NOT VALID");
                    return false;
                }

                Team team = new Team(new ArrayList<>(), problem);
                for (int t = 0; t < teamDescriptor.length; t++) {
                    String[] employeeDescriptor = teamDescriptor[t].split(Constants.SEPARATOR_DOT);
                    if (employeeDescriptor.length == 2) {
                        Integer ReplyOffice = Integer.parseInt(employeeDescriptor[0]) - 1;
                        Integer employeeNumber = Integer.parseInt(employeeDescriptor[1]) - 1;
                        if (ReplyOffice >= 0 && employeeNumber >= 0) {
                            OfficeReply officeReply = orMap.get(Constants.PREFIX_REPLY + ReplyOffice);
                            Employee employee = officeReply.getDipendentiMap().get(Constants.PREFIX_EMPLOYEE + employeeNumber);
                            GraphEdge edge = eMap.get(officeReply.getName() + problem.getCustomer().getName());
                            TeamMember tm = new TeamMember(employee, officeReply, edge);
                            team.getTeamMemberList().add(tm);
                            team.setName(Constants.PREFIX_TEAM + t);
                        } else {
                            System.out.println("ERROR OCCURRED. reply offices and/or employee number cannot be 0");
                            return false;
                        }
                    } else {
                        System.out.println("ERROR OCCURRED. OUTPUT FILE CORRUPTED ON LINE: " + (lineCounter - 1));
                        return false;
                    }
                }
                //tMap.put(team.getName(),team);
                tList.add(team);
            }
            return true;
        }
    }

    /**
     * TODO:
     * @return
     */
    private Float performScore(){
        System.out.println("********************************** performScore() function needs to be implemented ****************************");

        /*
        here we can use:
         - tList the team list
         - eMap the employee map
         - pMap the problem map
         - orMap the office reply map
         - ocMap the office customer map
         */
//
//        for(Team team: tList){
//            for (TeamMember teamMember: team.getTeamMemberList()) {
//                GraphEdge e = eMap.get(teamMember.getReply().getName() + team.getProblem().getCustomer().getName());
//                int i = e.getWeight().getDailyInterruptions();
//                float s = (100 - i) / 100 * team.getProblem().getDifficulty().getSoftware();
//                float h = (100 - i) / 100 * team.getProblem().getDifficulty().getHardware();
//
//                Float tetaP = team.getProblem().getDifficulty().getSoftware()
//            }
//        }
        return new Float(Float.MIN_VALUE);
    }
}
