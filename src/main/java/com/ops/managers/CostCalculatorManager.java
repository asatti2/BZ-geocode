package com.ops.managers;

import java.util.ArrayList;
import java.util.List;

import com.ops.dto.ResourceTO;

public class CostCalculatorManager {
	private List<ResourceTO> resourceList = new ArrayList<>();

	public CostCalculatorManager() {
		ResourceTO resourceBean = null;
		for(Resource resource : Resource.values()) {
			resourceBean = new ResourceTO(resource.getName(), resource.getCost(), resource.getCapecity());
			resourceList.add(resourceBean);
		}
	}

	public List<ResourceTO> costCalculate(int distance, int weight) {
		List<ResourceTO> finalResource = new ArrayList<>();

		for(int i=0; i<resourceList.size()-1; i++) {
			if (weight > resourceList.get(i+1).getCapecity()) {
				int number = weight / resourceList.get(i).getCapecity();
				resourceList.get(i).setNumber(number);
				finalResource.add(resourceList.get(i));
				weight = weight % resourceList.get(i).getCapecity();
				if(((double) weight / resourceList.get(i+1).getCapecity()) > 1) {
					resourceList.get(i).setNumber(resourceList.get(i).getNumber()+1);
					weight = 0;
					break;
				}
			}
		}
		if(weight>0) {
			int number = 0;
			if(weight < resourceList.get(resourceList.size() - 1).getCapecity())
				number = 1;
			else
				number = weight / 800;
			resourceList.get(resourceList.size() - 1).setNumber(number);
			finalResource.add(resourceList.get(resourceList.size() - 1));
		}

		return finalResource;
	}
}