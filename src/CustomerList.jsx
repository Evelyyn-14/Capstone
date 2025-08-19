
export function CustomerList(params){
    return (
      <div className="boxed">
      <h4>Customer List</h4>
          <table id="customer-list">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Pass</th>
              </tr>
            </thead>
            <tbody>
              {params.customers.map(
                (item, index) => {
                  return (<tr key={item.id} 
                  className={ (item.id === params.formObject.id )?'selected': ''}
                  onClick={()=>params.handleListClick(item)} 
                  >
                    <td>{item.name}</td>
                    <td>{item.email}</td>
                    <td>{item.password}</td>
                  </tr>);
                }
              )}
            </tbody>
          </table>
      </div>
    );
  }