function google() {
			window.open("https://bootstrapmb.com");
		}
		function bing() {
		    window.open("https://bootstrapmb.com");
		}
		var rollbackTo = false;
		var originalState = false;
		function storeState(stateWrapper) {
			rollbackTo = stateWrapper.current;
			//console.log("storeState called: ",rollbackTo);
		}
		function rollback(stateWrapper) {
			//console.log("rollback called: ", rollbackTo, originalState);
			//console.log("answers at the time of user input: ", stateWrapper.answers);
			if(rollbackTo!=false) {
				if(originalState==false) {
					originalState = stateWrapper.current.next;
						//console.log('stored original state');
				}
				stateWrapper.current.next = rollbackTo;
				//console.log('changed current.next to rollbackTo');
			}
		}
		function restore(stateWrapper) {
			if(originalState != false) {
				stateWrapper.current.next = originalState;
				//console.log('changed current.next to originalState');
			}
		}

		jQuery(function($){
			var convForm = $('#chat').convform();
			//console.log(convForm);
		});