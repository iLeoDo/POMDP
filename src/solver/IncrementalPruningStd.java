package solver;

import java.util.ArrayList;

import common.AlphaVector;
import model.BMDPImp;
import model.BMDP;
import model.POMDP;
import common.ValueFunctionImp;
import solver.iteration.Timer;
import solver.iteration.ValueIterationStd;
import solver.iteration.ValueIterationTimer;


public class IncrementalPruningStd extends ValueIterationStd {

	BMDP bmdp;
	private double delta;

	public IncrementalPruningStd(POMDP pomdp, double delta){
		startTimer();
		initValueIteration(pomdp);
		this.delta=delta;
		bmdp=new BMDPImp(pomdp);
		current = new ValueFunctionImp(pomdp.numS());
		current.push(new AlphaVector(bmdp.numS()));
		writeInitTime();
	}

	public Timer iterate() {
		startTimer();
		old=current;
		ValueIterationTimer
                iterationStats=(ValueIterationTimer) this.timer;
		current = new ValueFunctionImp(bmdp.numS());
		for(int a=0; a<bmdp.numA(); a++){
			// Perform Projections
			ArrayList<ValueFunctionImp> psi=new ArrayList<ValueFunctionImp>();
			for (int o=0;o<bmdp.numO();o++){
				ValueFunctionImp proj = new ValueFunctionImp(bmdp.numS());
				for (int idx=0;idx<old.size();idx++){
					AlphaVector alpha=old.getAlphaVector(idx);
					AlphaVector res=bmdp.projection(alpha, a, o);
					proj.push(res);
				}
				iterationStats.registerLp(proj.prune(delta));
				psi.add(proj);
			}
			ValueFunctionImp rewFunc=bmdp.getRewardValueFunction(a);
			//rewFunc.scale(1.0/(double)bmdp.numO());
			psi.add(rewFunc);
			//Now Cross sum...
			while (psi.size()>1){
				ValueFunctionImp vfA=psi.remove(0);
				ValueFunctionImp vfB=psi.remove(0);
				vfA.crossSum(vfB);
				iterationStats.registerLp(vfA.prune(delta));
				psi.add(vfA);
			}
			ValueFunctionImp vfA=psi.remove(0);
			current.merge(vfA);
		}
		iterationStats.registerLp(current.prune(delta));
		System.out.println(current.size());
		registerValueIterationStats();
    	return iterationStats;
	}

}